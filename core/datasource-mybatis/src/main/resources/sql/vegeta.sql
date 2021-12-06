# 建库
CREATE
DATABASE IF NOT EXISTS `vegeta` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8_bin;

use vegeta;

# 建表
drop database if exists tenant;
create table tenant
(
    id          bigint auto_increment                  not null,
    tenant_id   varchar(128)                           not null COMMENT '租户ID',
    tenant_name varchar(128)                           null COMMENT '租户名称',
    tenant_desc varchar(256) DEFAULT ''                not null COMMENT '租户介绍',
    owner       varchar(32)  DEFAULT '-'               not null COMMENT '负责人',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    creator     varchar(200) default ''                not null comment '创建人',
    updater     varchar(200) default ''                not null comment '更新人',
    deleted     int          default 0                 not null comment '删除标识',
    PRIMARY KEY (`id`),
    UNIQUE KEY `tenant_id` (`tenant_id`) comment '唯一索引'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='租户表';


drop database if exists app;
create table app
(
    id          bigint auto_increment                  not null,
    app_id      varchar(128)                           not null COMMENT 'appID',
    tenant_id   varchar(128)                           not null COMMENT '租户ID',
    app_name    varchar(128)                           null COMMENT 'app名称',
    app_desc    varchar(256) DEFAULT ''                not null COMMENT 'app介绍',
    owner       varchar(32)  DEFAULT '-'               not null COMMENT '负责人',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    creator     varchar(200) default ''                not null comment '创建人',
    updater     varchar(200) default ''                not null comment '更新人',
    deleted     int          default 0                 not null comment '删除标识',
    PRIMARY KEY (`id`),
    UNIQUE key `index_tenant_app` (`tenant_id`, `app_id`) comment '唯一主键'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='应用表';


drop database if exists thread_config;
create table thread_config
(
    id               bigint auto_increment                  not null,
    thread_pool_id   varchar(128)                           not null COMMENT '线程池ID',
    tenant_id        varchar(128)                           not null COMMENT '租户ID',
    app_id           varchar(128)                           not null COMMENT '应用ID',
    thread_pool_name varchar(128)                           null COMMENT '线程池名称',
    thread_pool_desc varchar(256) DEFAULT ''                not null COMMENT '线程池介绍',
    core_size        int(11)      DEFAULT NULL COMMENT '核心线程数',
    max_size         int(11)      DEFAULT NULL COMMENT '最大线程数',
    queue_type       int(11)      DEFAULT NULL COMMENT '队列类型...',
    capacity         int(11)      DEFAULT NULL COMMENT '队列大小',
    rejected_type    int(11)      DEFAULT NULL COMMENT '拒绝策略',
    keep_alive_time  int(11)      DEFAULT NULL COMMENT '线程存活时间',
    content          longtext COMMENT '线程池内容',
    md5              varchar(32)                            NOT NULL COMMENT '线程池参数 MD5加密',
    owner            varchar(32)  DEFAULT '-'               not null COMMENT '负责人',
    create_time      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    creator          varchar(200) default ''                not null comment '创建人',
    updater          varchar(200) default ''                not null comment '更新人',
    deleted          int          default 0                 not null comment '删除标识',
    PRIMARY KEY (`id`),
    UNIQUE key `index_tenant_app_thread` (`tenant_id`, `app_id`, thread_pool_id) comment '唯一主键'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='线程池配置表';

DROP TABLE IF EXISTS log_record_info;
CREATE TABLE log_record_info
(
    id          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    tenant      varchar(128)        NOT NULL DEFAULT '' COMMENT '租户标识',
    biz_key     varchar(128)        NOT NULL DEFAULT '' COMMENT '日志业务标识',
    biz_no      varchar(128)        NOT NULL DEFAULT '' COMMENT '业务码标识',
    operator    varchar(64)         NOT NULL DEFAULT '' COMMENT '操作人',
    action      varchar(128)        NOT NULL DEFAULT '' COMMENT '动作',
    category    varchar(128)        NOT NULL DEFAULT '' COMMENT '种类',
    detail      varchar(2048)       NOT NULL DEFAULT '' COMMENT '修改的详细信息，可以为json',
    create_time datetime                     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime                     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    creator     varchar(200)                 default '' not null comment '创建人',
    updater     varchar(200)                 default '' not null comment '更新人',
    deleted     int                          default 0 not null comment '删除标识',
    PRIMARY KEY (`id`),
    KEY `idx_biz_key` (`biz_key`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='操作日志表';