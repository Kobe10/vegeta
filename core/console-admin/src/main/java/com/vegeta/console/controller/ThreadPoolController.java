package com.vegeta.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.vegeta.config.model.CacheItem;
import com.vegeta.config.service.ConfigCacheService;
import com.vegeta.console.service.ThreadPoolService;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolDelReqDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolQueryReqDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolRespDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import com.vegeta.datasource.server.model.vo.ThreadPoolInstanceInfo;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import com.vegeta.global.model.InstanceInfo;
import com.vegeta.global.util.BeanUtil;
import com.vegeta.global.util.CollectionUtils;
import com.vegeta.global.util.StringUtils;
import com.vegeta.meta.core.BaseInstanceRegistry;
import com.vegeta.meta.core.Lease;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.vegeta.global.util.ContentUtil.getGroupKey;

/**
 * <p></p>
 * <p> xx
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年12月10日 14:55
 * @since 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/thread/pool")
public class ThreadPoolController {

    @Resource
    private ThreadPoolService threadPoolService;

    private final BaseInstanceRegistry baseInstanceRegistry;

    @PostMapping("/query/page")
    public Result<IPage<ThreadPoolRespDTO>> queryNameSpacePage(@RequestBody ThreadPoolQueryReqDTO reqDTO) {
        return Results.success(threadPoolService.queryThreadPoolPage(reqDTO));
    }

    @PostMapping("/query")
    public Result<ThreadPoolRespDTO> queryNameSpace(@RequestBody ThreadPoolQueryReqDTO reqDTO) {
        return Results.success(threadPoolService.getThreadPool(reqDTO));
    }

    @PostMapping("/save_or_update")
    public Result saveOrUpdateThreadPoolConfig(@RequestParam(value = "identify", required = false) String identify, @RequestBody ThreadPoolSaveOrUpdateReqDTO reqDTO) {
        threadPoolService.saveOrUpdateThreadPoolConfig(identify, reqDTO);
        return Results.success();
    }

    /**
     * 线程池下面的实例列表信息
     *
     * @param appId        应用
     * @param threadPoolId 线程池
     * @return com.vegeta.global.http.result.base.Result<java.util.List < com.vegeta.datasource.server.model.vo.ThreadPoolInstanceInfo>>
     * @Author fuzhiqiang
     * @Date 2021/12/10
     */
    @GetMapping("/list/instance/{appId}/{threadPoolId}")
    public Result<List<ThreadPoolInstanceInfo>> listInstance(@PathVariable("appId") String appId, @PathVariable("threadPoolId") String threadPoolId) {
        List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(appId);
        Lease<InstanceInfo> first = CollectionUtils.getFirst(leases);
        if (first == null) {
            return Results.success(Lists.newArrayList());
        }

        InstanceInfo holder = first.getHolder();
        String itemTenantKey = holder.getGroupKey();
        String groupKey = getGroupKey(threadPoolId, itemTenantKey);
        Map<String, CacheItem> content = ConfigCacheService.getContent(groupKey);

        List<ThreadPoolInstanceInfo> returnThreadPool = Lists.newArrayList();
        content.forEach((key, val) -> {
            ThreadPoolInstanceInfo threadPoolInstanceInfo = BeanUtil.convert(val.configAllInfo, ThreadPoolInstanceInfo.class);
            threadPoolInstanceInfo.setClientAddress(StringUtils.subBefore(key, Constants.IDENTIFY_SLICER_SYMBOL, false));
            threadPoolInstanceInfo.setIdentify(key);
            threadPoolInstanceInfo.setClientBasePath(holder.getClientBasePath());
            returnThreadPool.add(threadPoolInstanceInfo);
        });

        return Results.success(returnThreadPool);
    }

    @DeleteMapping("/delete")
    public Result deletePool(@RequestBody ThreadPoolDelReqDTO reqDTO) {
        threadPoolService.deletePool(reqDTO);
        return Results.success();
    }

    @PostMapping("/alarm/enable/{id}/{isAlarm}")
    public Result alarmEnable(@PathVariable("id") String id, @PathVariable("isAlarm") Integer isAlarm) {
        threadPoolService.alarmEnable(id, isAlarm);
        return Results.success();
    }
}
