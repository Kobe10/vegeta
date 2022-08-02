# 动态线程池平台

## 项目名称

vegeta

- 贝吉塔
- ps:  纯粹因为喜欢七龙珠，贝吉塔：想要变的更强的男二号，符合各位搬砖的人设

## 背景

    1、自己一直在看各种注册发现中心、配置中心的源码，看源码的过程中去自己实现一个轮子，可以更加深刻的理解其中的奥妙和精髓；
    2、我看开源市面上比较少有开源这方面东西的项目，所以想自己动手实现一个符合企业级应用的线程池管理平台；

## 定位

<h5 style="color: firebrick">线程池管理平台</h5>

## 能力
待定


## 项目部署
### 项目结构

```
├── base
│   ├──base-parent                                                根项目
│   ├──公共组件模块
│   │   ├──common-core                                            核心公共包
│   │   ├──github-change-util                                     github项目监听组件
│   │   └──log-util                                               日志工具包 (强大通用)
│   ├──demo（示例项目）
│   │   └── demo-web                                              demo示例
│   └──starter                                                    核心组件
│       ├── auth                                                  权限模块
│       ├── client                                                客户端-sdk形式        
│       ├── config-server                                         服务端 (和client对应)
│       ├── console-admin                                         后台管理模块                                  
│       ├── console-ui                                            前端(vue)
│       ├── datasource-mybatis                                    orm模块
│       ├── meta-server                                           元数据服务(前期心跳服务)
│       └── server-web                                            打包 (config-server、meta-server、console-admin)到一个包底下  前期一起部署     --  独立部署
```

### 环境部署

#### 开发环境

- jdk：1.8+
- gradle：7.0+
- springboot：2.3.5

#### 本地部署

- 切勿点击idea的自动导入gradle项目提示；只需要找到base-parent目录下面的build.gradle文件，右键然后点击 `import gradle project`
  这样就能自动导入所有模块了，不然的话还得手动删掉其他的多余模块
- 启动server-web
- 启动client-web

### 快捷地址

<details>
<summary>点开有惊喜</summary>

* [`base-parent`](#base-parent)
* [`common-core`](#common-core)
* [`log-util`](#log-util)
* [`demo-web`](#demo-web)
* [`auth`](#auth)
* [`client`](#client)
* [`config-server`](#config-server)
* [`console-admin`](#console-admin)
* [`console-ui`](#console-ui)
* [`datasource-mybatis`](#datasource-mybatis)
* [`meta-server`](#meta-server)
* [`server-web`](#server-web)

</details>

### 版本升级记录

#### 2021-11-04 付志强

v0.0.1 初始化 0.0.1

## 技术栈

不使用任何中间件，降低接入成本
`springboot`  + `gradle`

## 最后
