package com.vegeta.datasource.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import com.vegeta.global.enums.DelEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Meta object handler.
 *
 * @author chen.ma
 * @date 2021/7/1 22:43
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    // 处理insert  update的字段
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());

        this.strictInsertFill(metaObject, "deleted", Integer.class, DelEnum.NORMAL.getIntCode());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }
}
