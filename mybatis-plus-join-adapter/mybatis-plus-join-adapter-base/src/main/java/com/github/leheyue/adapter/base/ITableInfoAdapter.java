package com.github.leheyue.adapter.base;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.function.Supplier;

/**
 * @author yulichang
 * @since 1.4.3
 */
public interface ITableInfoAdapter {

    boolean mpjHasLogic(TableInfo tableInfo);

    boolean mpjIsPrimitive(TableFieldInfo tableFieldInfo);

    TableFieldInfo mpjGetLogicField(TableInfo tableInfo);

    boolean mpjHasPK(TableInfo tableInfo);

    Configuration mpjGetConfiguration(TableInfo tableInfo);

    Field mpjGetField(TableFieldInfo fieldInfo, Supplier<Field> supplier);
}
