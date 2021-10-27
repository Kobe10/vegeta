# 注意
切勿点击idea的自动导入gradle项目提示；只需要找到base-parent目录下面的build.gradle文件，右键然后点击 `import gradle project`
这样就能自动导入所有模块了，不然的话还得手动删掉其他的多余模块
## module  依赖图
![](https://tva1.sinaimg.cn/large/008i3skNly1gri2nwvhqoj30na0t6adx.jpg)


# 小班课基础组件
## 项目名称
### 真实姓名 
朱雀 -- 四大神兽之一
ps:  实在想不出高大尚的名字了

### 小名
小班课基础组件

## 背景

    小班课存在N个微服务(项目)，各个微服务都有一些公共的组件，各种mapper、dao、entity的重复实体；存在多种http的调用；
    存在各种redis的工具类、配置；后续可能也会有各种通用化的配置

## 目的

    提供基础的通用能力，赋能各个微服务；降本增效、提高研发效率；

## 定位

<h5 style="color: firebrick">以提供基础能力为中心，吸收并辐射一些公共能力的基础服务，开箱即用，沉淀后可以外部输出！</h5>

## 小班课基础组件使用手册

### 修改记录

#### 2021-06-01 付志强

v1.0 初始化 1.0.1

#### 2020-06-16 xxx

版本升级 xxx

### 项目结构

```
├── base
│   ├──base-parent
│   ├──xbk-common-core
│   │   ├──xbk-common-core-global
│   │   └──xbk-common-core-utils
│   ├──xbk-common-demo（示例项目）
│   │   ├── xbk-common-demo-feign-api
│   │   ├── xbk-common-demo-web
│   └──xbk-common-starter
│       ├── xbk-common-starter-datasource
│       ├── xbk-common-starter-es
│       ├── xbk-common-starter-httpclient
│       ├── xbk-common-starter-mybatis
│       ├── xbk-common-starter-redisThreadPoolTaskFactory
│       └── xbk-common-starter-web
```

### 快捷地址

<details>
<summary>点开有惊喜</summary>

* [`xbk-base-parent`](#base-parent)
* [`xbk-common-core-global`](#xbk-common-core-global)
* [`xbk-common-core-utils`](#xbk-common-core-utils)
* [`xbk-common-demo-feign-api`](#xbk-common-demo-feign-api)
* [`xbk-common-demo-web`](#xbk-common-demo-web)
* [`xbk-common-starter-es`](#xbk-common-starter-es)
* [`xbk-common-starter-datasource`](#xbk-common-starter-datasource)
* [`xbk-common-starter-httpclient`](#xbk-common-starter-httpclient)
* [`xbk-common-starter-mybatis`](#xbk-common-starter-mybatis)
* [`xbk-common-starter-redis`](#xbk-common-starter-redis)
* [`xbk-common-starter-web`](#xbk-common-starter-web)

</details>

---

## base-parent

<details>
<summary>jar版本管理</summary>

### 描述

1. 定义了所有`jar`包版本信息、`gradle`常用的组件信息、`gradle`配置信息。
2. 所有的工具包的`parent`。
3. 统一的`gradle`版本管理，有效的避免不同`gradle`组件包版本冲突、不同`gradle`组件包版本不一致导致不必要包引用。

### 规约

1. 所有项目`build.gradle`都应继承`base-parent` (subprojects 中已经定义)。
2. 应当避免在业务项目模块中二次声明`gradle`组件版本信息。

### 使用示例

1. 在settings目录中增加对应模块的模块名称即可

```
include(':xbk-common-core')
include(':xbk-common-core:xbk-common-core-util')
include(':xbk-common-core:xbk-common-core-global')
project(':xbk-common-core:xbk-common-core-util').projectDir = file('../xbk-common-core/xbk-common-core-util')
project(':xbk-common-core:xbk-common-core-global').projectDir = file('../xbk-common-core/xbk-common-core-global')
project(':xbk-common-core').projectDir = file('../xbk-common-core')
```

</details>

---

## xbk-common-core-global

<details>
<summary>全局静态属性</summary>

### 描述

1. 定义了系统公共`Nacos`配置的`NameSpace`信息。
2. 定义了系统异常`BizException`。
3. 定义了系统交互所有已约定的`Head`信息。
4. 定义了`web`返回数据封装结果类型。

### 规约

1. 所有组件开发中使用的基础信息都应以此为准，不应该额外定义。

</details>

---

## xbk-common-core-utils

<details>
<summary>静态工具方法</summary>

### 描述

1. 提供一些常用de开发静态类
2. 提供一些常用的业务工具类

### 规约

1. 该模块中提供的方法必须都为静态方法。

### 使用示例

各种工具类吧，想用什么就往里面加吧，不过最主要的是不要重复引入一些想用技术的工具类，没必要；

</details>

---

## xbk-common-demo-feign-api

<details>
<summary>feign公共API</summary>

### 描述

1. 初步想法是提供所有的外部服务的feign接口，所有微服务都通过nacos注册，通过feign调用

### 规约

1. 该模块只是提供一个接口调用能力。

### 使用示例

暂时还没想好，有待讨论
</details>

---

## xbk-common-demo-web

<details>
<summary>一个基于该组件搭建的一个demo示例项目</summary>

### 描述

1. 单纯一个demo项目

### 规约

1. 暂时还没有写，有待讨论(主要是分层思路)

### 使用示例

暂时还没想好，有待讨论
</details>


---

## xbk-common-starter-datasource

<details>
<summary>基于spring-boot配置的多数据源自动注入、mybatis组件自动装配</summary>

### 现状

1. 目前小班课是单点数据源，没有多数据源的情况；多数据源的情况适用于之后的分库分表、读写分离的情况，所以目前先预留空间； 根据以往经验，这种需求还是存在的，所以先保留了
2. mybatis的话现阶段系统中有使用mybatis-plus的，也有自定义mapper.xml文件的；现阶段任务主要兼容这两种主流开发方式吧

### 描述 (当前版本的读写分离或者mybatis都不太完善，需要进一步优化)

1. `dataSource` 提供了基于`spring-boot`配置的多自动数据源注入(这个功能在数据迁移、读写分离等的的时候可能需要用到)、
   `mybatis`组件自动装配、`Druid`监控自动装配。
2. 依赖`com.alibaba.druid`：阿里的数据库连接工具。
3. 依赖`tk.mybatis.mapper-spring-boot-starter`：`mybatis`增强工具，如果引入该模块之后不使用`mybatis`自动装配可排除。
5. 自动集成`pagehelper`: `mybatis`分页组件。

### 使用 (数据源配置信息托管到nacos或者apollo等等)

1. 强烈建议排除`DataSource`自动注入。
    ```java
    @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
    ```
2. 单数据源使用：引用`Nacos`公共配置`XBK-MySql`，覆盖原有配置中`url`、`username`、`password`。
3. 多数据源使用：引用`Nacos`公共配置`XBK-Mysql`，覆盖原有配置中`url`、`username`、`password`即可注入`master`
   数据源，其他数据源配置为`datasource.dynamicDataSource.xx`，其中`xx`为数据源名称。 例如，配置`read`数据源。
    ```yml
    spring:
      datasource:
        dynamicDataSource:
          read:
            url: xx
            username: xxx
            password: xx
    ```
   在`Nacos`中默认提供了`read`/`write`两个扩展数据源配置--读写分离配置化。代码调用：
    ```java
    @DataSourceType("read")
    public List<Demo> selectFromReadDataSource(String name) {
        return demoMapper.customizeSqlSelectByName(name);
    }
    ```
   此处建议把数据源名称声明成静态常量，方便全局统一管理。

### 扩展

待优化，优先支持mybatis-plus组件的支持
</details>

---

## xbk-common-starter-es

<details>
<summary>公共ES自动注入</summary>

### 描述

1. 目前因为还没有到使用ES的阶段，所以这里只是预留空间
2. 提供了`Es`自动注入。

### 使用

1. 关联`Nacos`配置`XBK-ES`。
2. 引入必要的`ES`配置。`@EnableDataCenterEs`: 开启`DataCenteres`;`@EnableXbkEs`开启`XbkEs`。
    ```java
    @Configuration
    @EnableDataCenterEs
    @EnableXbkEs
    public class EsConfig {
    }
    ```
   由于`ES`连接在启动时候会检测连接情况，因此建议按需引入，及时排除不必要的引入。

### 扩展
---
</details>

## xbk-common-starter-httpclient

<details>
<summary>Http网络请求工具</summary>

### 描述 (基础信息配置化)

1. 提供了`http`请求工具。
2. 提供了`httpClient`、`okhttp`两种实现方式。
3. 提供了`feign`通信方式。

### 使用

1. 关联`Nacos`配置`XBK-HttpClient`。
2. 使用`RestTemplate`发送请求

```java
private final RestTemplate restTemplate;
//get 请求
        CommonResponse<String> commonResponse=restTemplate.getForObject("http://localhost:8080/demo/demo/testRedis",CommonResponse.class);
//post 请求
        DemoDTO demoDTO=new DemoDTO();
        demoDTO.setName("name");
        demoDTO.setNum(1);
        CommonResponse commonResponse=restTemplate.postForObject("http://localhost:8080/demo/demo",demoDTO,CommonResponse.class);

```

3. 使用静态工具发送请求

```java
//使用 httpClient 发送 get请求
HttpClientUtil.get();
//使用 okhttp 发送 get 请求
        OkHttpUtil.get();
```

### 扩展

1. 切换当前`Http`请求实现。

```xml
spring.http.rest-client.type = okHttp3 //默认
        spring.http.rest-client.type = httpClient //使用 httpClient
```

</details>

---

---

## xbk-common-starter-mybatis

<details>
<summary>基于tk.mybatis.mapper.starter 简单sql工具、pagehelper-spring-boot-starter分页工具</summary>

#### 描述

1. 提供基于`tk.mybatis.mapper.starter` 简单`sql`工具、`pagehelper-spring-boot-starter`分页工具。
2. 提供基于`com.baomidou:mybatis-plus-boot-starter:3.4.1`：`mybatis-plus`增强工具.
3. 建议配合`common-util-dataSource`一起使用，可以快速实现多数据源、持久层集成。
4. 该组件未提供`mybatis`相关的`bean`装配信息，可以自行定义。

#### 使用

该工具提供了两套工具，可以根据当前的业务需求自行选择。

+ 实体`Entity extend BaseEntityOnlyId`：工具只会自动处理`id`、`createTime`：创建时间、`lastModifyTime`：修改时间。
+ 实体`Entity extend BaseEntity`：工具会额外维护`isDel`: 数据状态。
+ **如果项目中只有逻辑删除，不做物理删除，请使用`BaseEntity`，提供了一套完整的 逻辑`CRUD`操作**

1. 定义`mybatis-mapper`扫描包路径
      ```java
      @Configuration
      @MapperScan(basePackages = {DataSourceCommon.MAPPER_PATH, DynamicMapperPackage.DYNAMIC_PACKAGE_PATH})
      public class DataSourceCommon {
          /**
           * 定义Mapper包路径
           */
          public static final String MAPPER_PATH = "xxx.mapper";
      }
      ---
      DynamicMapperPackage.DYNAMIC_PACKAGE_PATH 为 自定义扩展的 sql 工具，此处必须声明。
      ```
2. 声明`Entity`
      ```java
      @Data
      @EqualsAndHashCode(callSuper = true)
      @Table(name = "demo")
      public class Demo extends BaseEntity {
          /**
           * 测试 - 名称 当数据库字段与实体字段不一致时候
           */
          @Column(name = "demo_name")
          private String name;
    
          /**
           * 示例 - 字符 当数据库字段与实体字段一致
           */
          private Integer demoNum;
      }
      ```
3. 声明`Mapper`
      ```java
      @Repository
      public interface DemoMapper extends Mapper<Demo> {
      }
      ```
4. 声明`service`
      ```java
      public interface DemoService extends BaseDecoratorService<Demo> {
      }
      ```
5. 声明`serviceImpl`
      ```java
      @Service
      @RequiredArgsConstructor
      public class DemoServiceImpl extends BaseDecoratorServiceImpl<Demo> implements DemoService {
      }
      ```

至此，在注入`@Autowired DemoService demoService`，即可调用基础的`CRUD`方法。

#### 扩展

1. 自定义`mybatis` 相关配置：根据官方自动配置即可，需要在`@MapperScan`中额外声明`DynamicMapperPackage.DYNAMIC_PACKAGE_PATH`路径。**
   此处建议使用`tk.mybatis.spring.annotation.MapperScan`**

2. 自定义`sql`语句

+ 定义`SqlProvider`。

 ```java
   public class DemoSqlProvider {
    /**
     * 自定义sql 根据 `name` 模糊查询
     */
    public String customizeSqlSelectByName(@Param("name") final String name) {
        String table = SqlHelper.getDynamicTableName(Demo.class, Demo.class.getAnnotation(Table.class).name());
        String allColumns = SqlHelper.getAllColumns(Demo.class);
        return new SQL() {{
            SELECT(allColumns);
            FROM(table);
            WHERE("demo_name like CONCAT('%',#{name},'%')");
        }}.toString();
    }
}
 ```

+ 在`Mapper`声明方法。
   ```java
    @Repository
    public interface DemoMapper extends Mapper<Demo> {
        /**
         * 自定义sql 根据 `name` 模糊查询
         */
        @SelectProvider(type = DemoSqlProvider.class, method = "customizeSqlSelectByName")
        List<Demo> customizeSqlSelectByName(@Param("name") String name);
    }
    ```

3. 模糊查询语法支持和分页语法支持
    ```java
   WeekendSqls<Demo> demoWeekendSql = WeekendSqls.<Demo>custom()
           .andLike(Demo::getName, "%" + name + "%");
   Example example = Example.builder(Demo.class)
           .andWhere(demoWeekendSql)
           .build();
   PageInfo<Demo> pageInfo = PageHelper.startPage(pageNum, pageSize)
           .doSelectPageInfo(() -> demoService.selectByExample(example));
    ```
4. 使用`xml`文件 文件存放目录：`classpath:mybatis/mapper/*.xml`. 由于`Mapper`继承了`BaseMapper`,应避免与已存在方法重名。

</details>

---

## xbk-common-starter-redis

<details>
<summary>基于spring-boot-starter-data-redis工具改写</summary>

### 描述

+ 提供基于`spring-boot-starter-data-redis`工具，实现了`key`服务隔离、环境隔离。
+ 提供基于`reids`实现的分布式锁（当然，用起来太费劲）。
+ 后续提供基于`redisson`实现的分布式锁。

### 使用

1. 关联`Nacos`配置`XBK-Redis`。
2. 配置`key`隔离前缀，优先匹配`spring.redis.prefix`，如果不存在，会使用`application-name`，如果依旧不存在会使用**unknown**。
      ```yml
      spring:
        redis:
          prefix: prefix
        application:
          name: serverName
       ```
3. 使用redis 锁。
     ```java
     @Autowired private DistributedLock distributedLock;
    
     final String lockKey = "lockKey";
     distributedLock.lock(lockKey);
     .....
     distributedLock.releaseLock(lockKey);
     ```
   **加锁、释放锁必须在同一线程中进行，否则会释放锁失败**

### 扩展

1. 使用不带前缀的`RedisTemplate`。
      ```java
      @Autowired @Qualifier(Common.REDIS_TEMPLATE_WITHOUT_PREFIX_NAME)
      private RedisTemplate redisTemplate;
      ```

</details>

## xbk-common-starter-web

<details>
<summary>spring-web 项目基础配置</summary>

### 描述

1. 提供全局统一返回结果集封装。
2. 提供全局异常处理机制。
3. 提供全局`json`序列化配置。
5. 提供日志打印配置。
6. 自动注入了两个任务线程池。
7. 提供全局参数校验配置。

+ **该包在各个项目组中应该在项目初始化时就定义所有信息，不建议该包进行扩展配置。**

### 使用

1. 关联`Nacos`基础配置`XBK-BASE`
2. `controller`正常返回的`objct`会被额外封装
    ```json
    {
        "code" : 200,
        "message" : "操作成功",
        "data" : {},
        "error_code" : 200,
        "error_message" : ""
    }
    ```
3. `controller`调用`void`方法
    ```java
    return SuccessResponse.defaultSuccessResponse();
    ```
4. 业务异常处理
   ```java
   throw new BizException(100101, "业务异常");
   ```

### 扩展

1. 自定义异常处理。异常处理提供了两个扩展`bean`：`ExceptionResolver`异常信息接受器，该`bean`可以注册多个，捕获异常后会根据`order`排序依次调用所有`bean`
   的处理方法。`ExceptionResultHandler` 异常结果处理器，该`bean`只可以注册一个，在所有`ExceptionResolver`处理完成之后调用该`bean`方法。系统现在默认提供：
    + `DefaultExceptionResultHandler`：默认异常结果处理，**该`bean`不建议覆盖**。
    + `DefaultBizExceptionResolver`：业务异常接收器。
    + `DefaultRuntimeExceptionResolver`：系统异常接收器。 自定义异常接收器：
      ```java
      @Configuration
      @Slf4j
      public class ExceptionLog implements ExceptionResolver {
          @Override
          public void resolve(HttpServletRequest request, Exception exception) {
            String prefix = exception instanceof BizException ? "【业务异常】" : "【系统异常】";
            log.error(prefix + ExceptionUtils.getStackTrace(exception));
          }
      
          @Override
          public boolean canResolve(HttpServletRequest request, Exception exception, HttpStatus httpStatus) {
              return exception instanceof BizException || exception instanceof RuntimeException;
          }
      }
      ```
2. 扩展异常处理器，如果系统提供的异常处理规则不满足 当前项目时候可以扩展处理，也可以通过扩展`ExceptionAdviceAutoConfiguration`来达到相同的效果。
     ```java
     @Configuration
     @Order(Ordered.HIGHEST_PRECEDENCE + 101)
     public class ExceptionAdviceConfiguration extends ExceptionAdviceAutoConfiguration {
   
         @ExceptionHandler({HttpRequestMethodNotSupportedException.class, NoHandlerFoundException.class})
         @Override
         public Result notFoundHandler(HttpServletRequest request, Exception exception) {
             return ResultSuccess.defaultResultSuccess();
         }
     }
    ``` 
3. 扩展参数解析器，如果当前项目中集成了其他的`web`项目，例如`swagger`，统一的增强结果封装也会封装这类的请求接口从而导致`web`组件失效，此时可以通过扩展参数解析来解决。
      ```java
      @RestControllerAdvice
      public class ResponseAdviceConfiguration extends AbstractResponseAdviceTemplate {
    
          //定义需要 返回结果需要被封装的 包路径 建议直接返回`controller`路径
          private static final List<String> SUPPORT_PATH = Lists.newArrayList("com.xx.controller");
    
          @Override
          public List<String> supportPath() {
              return SUPPORT_PATH;
          }
      }
      如果如上依旧不能满足需求，可以考虑重写 supports 方法
      ```

</details>

------

## 技术栈

`springboot` + `springcloud` + `gradle` + `各种中间件`

## 最后

鸣谢：付志强、王涛、张明

