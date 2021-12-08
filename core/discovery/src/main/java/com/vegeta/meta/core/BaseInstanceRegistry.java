package com.vegeta.meta.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.vegeta.global.model.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.vegeta.global.consts.Constants.EVICTION_INTERVAL_TIMER_IN_MS;
import static com.vegeta.global.consts.Constants.SCHEDULED_THREAD_CORE_NUM;

/**
 * Base instance registry.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/8
 */
@Slf4j
@Service
public class BaseInstanceRegistry implements InstanceRegistry<InstanceInfo> {

    private final int CONTAINER_SIZE = 1024;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock read = readWriteLock.readLock();

    protected final Object lock = new Object();

    /**
     * 内存存储 所有注册实例信息
     * key --- >  appName   value : {key ---> instanceId   value ----> Lease<InstanceInfo>}
     */
    private final ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry = new ConcurrentHashMap<>(CONTAINER_SIZE);

    protected volatile int expectedNumberOfClientsSendingRenews;

    private final CircularQueue<Pair<Long, String>> recentRegisteredQueue;

    private final CircularQueue<Pair<Long, String>> recentCanceledQueue;

    //
    private ConcurrentLinkedQueue<RecentlyChangedItem> recentlyChangedQueue = new ConcurrentLinkedQueue<>();

    /**
     * 实例状态枚举的 缓存 (有效期1小时)
     * key --- >  instanceId   value :  InstanceStatus 枚举
     */
    protected final ConcurrentMap<String, InstanceInfo.InstanceStatus> overriddenInstanceStatusMap = CacheBuilder.newBuilder().initialCapacity(512).expireAfterAccess(1, TimeUnit.HOURS).<String, InstanceInfo.InstanceStatus>build().asMap();

    public BaseInstanceRegistry() {
        this.recentRegisteredQueue = new CircularQueue(CONTAINER_SIZE);
        this.recentCanceledQueue = new CircularQueue(CONTAINER_SIZE);
    }

    @Override
    public List<Lease<InstanceInfo>> listInstance(String appName) {
        Map<String, Lease<InstanceInfo>> appNameLeaseMap = registry.get(appName);
        if (CollectionUtils.isEmpty(appNameLeaseMap)) {
            return Lists.newArrayList();
        }

        List<Lease<InstanceInfo>> appNameLeaseList = Lists.newArrayList();
        appNameLeaseMap.values().forEach(each -> appNameLeaseList.add(each));
        return appNameLeaseList;
    }

    @Override
    public void register(InstanceInfo registrant) {
        // todo 这里有并发问题
        read.lock();
        try {
            // 获取当前应用的 所有注册实例信息
            Map<String, Lease<InstanceInfo>> registerMap = registry.get(registrant.getAppName());
            if (registerMap == null) {
                ConcurrentHashMap<String, Lease<InstanceInfo>> registerNewMap = new ConcurrentHashMap<>(12);
                registerMap = registry.putIfAbsent(registrant.getAppName(), registerNewMap);
                if (registerMap == null) {
                    registerMap = registerNewMap;
                }
            }
            // 获取当前应用的某个实例的信息   (取最近在线的实例信息)
            Lease<InstanceInfo> existingLease = registerMap.get(registrant.getInstanceId());
            if (existingLease != null && (existingLease.getHolder() != null)) {
                Long existingLastDirtyTimestamp = existingLease.getHolder().getLastDirtyTimestamp();
                Long registrationLastDirtyTimestamp = registrant.getLastDirtyTimestamp();

                if (existingLastDirtyTimestamp > registrationLastDirtyTimestamp) {
                    registrant = existingLease.getHolder();
                }
            }
            // 构建实例信息
            Lease<InstanceInfo> lease = new Lease<>(registrant);
            // 初始化 实例启动时间
            if (existingLease != null) {
                lease.setServiceUpTimestamp(existingLease.getServiceUpTimestamp());
            }
            registerMap.put(registrant.getInstanceId(), lease);
            //
            recentRegisteredQueue.add(new Pair<>(System.currentTimeMillis(), registrant.getAppName() + "(" + registrant.getInstanceId() + ")"));

            // 更新实例的状态缓存
            InstanceInfo.InstanceStatus overriddenStatusFromMap = overriddenInstanceStatusMap.get(registrant.getInstanceId());
            if (overriddenStatusFromMap != null) {
                log.info("Storing overridden status :: {} from map", overriddenStatusFromMap);
                registrant.setOverriddenStatus(overriddenStatusFromMap);
            }

            // 实例 UP; 服务up  更新实例启动时间
            if (InstanceInfo.InstanceStatus.UP.equals(registrant.getStatus())) {
                lease.serviceUp();
            }

            registrant.setActionType(InstanceInfo.ActionType.ADDED);
            // 将实例续约信息 放入阻塞队列 ，由阻塞队列进行统一注册
            recentlyChangedQueue.add(new RecentlyChangedItem(lease));
            registrant.setLastUpdatedTimestamp();
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean renew(InstanceInfo.InstanceRenew instanceRenew) {
        String appName = instanceRenew.getAppName();
        String instanceId = instanceRenew.getInstanceId();
        // 从内存中或者实例的租约信息
        Map<String, Lease<InstanceInfo>> registryMap = registry.get(appName);
        Lease<InstanceInfo> leaseToRenew = null;
        if (registryMap == null || (leaseToRenew = registryMap.get(instanceId)) == null) {
            return false;
        }
        // 更新实例租约信息
        leaseToRenew.renew();
        return true;
    }

    @Override
    public void remove(InstanceInfo info) {
        ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            String appName = info.getAppName();
            String instanceId = info.getInstanceId();
            Map<String, Lease<InstanceInfo>> leaseMap = registry.get(appName);
            if (CollectionUtils.isEmpty(leaseMap)) {
                log.warn("Failed to remove unhealthy node, no application found :: {}", appName);
                return;
            }

            Lease<InstanceInfo> remove = leaseMap.remove(instanceId);
            if (remove == null) {
                log.warn("Failed to remove unhealthy node, no instance found :: {}", instanceId);
                return;
            }

            log.info("Remove unhealthy node, node ID :: {}", instanceId);
        } finally {
            writeLock.unlock();
        }
    }

    static class CircularQueue<E> extends AbstractQueue<E> {

        private final ArrayBlockingQueue<E> delegate;

        public CircularQueue(int capacity) {
            this.delegate = new ArrayBlockingQueue<>(capacity);
        }

        @Override
        public Iterator<E> iterator() {
            return delegate.iterator();
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean offer(E e) {
            while (!delegate.offer(e)) {
                delegate.poll();
            }
            return true;
        }

        @Override
        public E poll() {
            return delegate.poll();
        }

        @Override
        public E peek() {
            return delegate.peek();
        }

        @Override
        public void clear() {
            delegate.clear();
        }

        @Override
        public Object[] toArray() {
            return delegate.toArray();
        }

    }

    private static final class RecentlyChangedItem {
        private long lastUpdateTime;

        private Lease<InstanceInfo> leaseInfo;

        public RecentlyChangedItem(Lease<InstanceInfo> lease) {
            this.leaseInfo = lease;
            lastUpdateTime = System.currentTimeMillis();
        }

        public long getLastUpdateTime() {
            return this.lastUpdateTime;
        }

        public Lease<InstanceInfo> getLeaseInfo() {
            return this.leaseInfo;
        }
    }

    /**
     * 执行清理过期租约逻辑
     *
     * @param additionalLeaseMs long
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    public void evict(long additionalLeaseMs) {
        // 获取到期的租约
        List<Lease<InstanceInfo>> expiredLeases = Lists.newArrayList();

        for (Map.Entry<String, Map<String, Lease<InstanceInfo>>> groupEntry : registry.entrySet()) {
            Map<String, Lease<InstanceInfo>> leaseMap = groupEntry.getValue();
            if (leaseMap != null) {
                for (Map.Entry<String, Lease<InstanceInfo>> leaseEntry : leaseMap.entrySet()) {
                    Lease<InstanceInfo> lease = leaseEntry.getValue();
                    if (lease.isExpired(additionalLeaseMs) && lease.getHolder() != null) {
                        expiredLeases.add(lease);
                    }
                }
            }
        }

        // 遍历租约实例   移除租约过期实例
        for (Lease<InstanceInfo> expiredLease : expiredLeases) {
            String appName = expiredLease.getHolder().getAppName();
            String id = expiredLease.getHolder().getInstanceId();
            internalCancel(appName, id);
        }
    }

    /**
     * 取消实例
     *
     * @param appName
     * @param id
     * @return boolean
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    protected boolean internalCancel(String appName, String id) {
        read.lock();
        try {
            Map<String, Lease<InstanceInfo>> registerMap = registry.get(appName);
            if (!CollectionUtils.isEmpty(registerMap)) {
                registerMap.remove(id);
                log.info("Clean up unhealthy nodes. Node id :: {}", id);
            }
        } finally {
            read.unlock();
        }

        return true;
    }

    /**
     * 清理过期租约的定时任务
     *
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    public class EvictionTask extends TimerTask {

        /**
         * 最后任务执行时间
         */
        private final AtomicLong lastExecutionNanosRef = new AtomicLong(0L);

        @Override
        public void run() {
            try {
                // 获取 补偿时间毫秒数
                long compensationTimeMs = getCompensationTimeMs();
                log.info("Running the evict task with compensationTime {} ms", compensationTimeMs);
                // 清理过期租约逻辑
                evict(compensationTimeMs);
            } catch (Throwable e) {
                log.error("Could not run the evict task", e);
            }
        }

        long getCompensationTimeMs() {
            long currNanos = getCurrentTimeNano();
            long lastNanos = lastExecutionNanosRef.getAndSet(currNanos);
            if (lastNanos == 0L) {
                return 0L;
            }

            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(currNanos - lastNanos);
            long compensationTime = elapsedMs - EVICTION_INTERVAL_TIMER_IN_MS;
            return Math.max(compensationTime, 0L);
        }

        long getCurrentTimeNano() {
            return System.nanoTime();
        }
    }

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(SCHEDULED_THREAD_CORE_NUM, new ThreadFactoryBuilder().setNameFormat("registry-eviction").setDaemon(true).build());

    private final AtomicReference<EvictionTask> evictionTaskRef = new AtomicReference<>();

    /**
     * 初始化 清理过期租约的定时任务   60s 执行一次   启动之后延迟60s执行一次
     *
     * @return void
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    public void postInit() {
        evictionTaskRef.set(new EvictionTask());
        scheduledExecutorService.scheduleWithFixedDelay(evictionTaskRef.get(), EVICTION_INTERVAL_TIMER_IN_MS, EVICTION_INTERVAL_TIMER_IN_MS, TimeUnit.MILLISECONDS);
    }
}
