package com.github.yulichang.wrapper.segments;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.yulichang.toolkit.support.FieldCache;
import com.github.yulichang.wrapper.enums.BaseFuncEnum;
import org.apache.ibatis.type.TypeHandler;

/**
 * 实体属性列
 *
 * @author yulichang
 * @since 1.3.12
 */
public class SelectProperty implements Select {
    private final FieldCache cache;

    private final String alias;

    private final boolean hasTableAlias;

    private final String tableAlias;

    public SelectProperty(FieldCache cache, boolean hasTableAlias, String tableAlias) {
        this.cache = cache;
        this.alias = null;
        this.hasTableAlias = hasTableAlias;
        this.tableAlias = tableAlias;
    }

    public SelectProperty(FieldCache cache, String alias, boolean hasTableAlias, String tableAlias) {
        this.cache = cache;
        this.alias = alias;
        this.hasTableAlias = hasTableAlias;
        this.tableAlias = tableAlias;
    }

    @Override
    public Class<?> getClazz() {
        return cache.field.getDeclaringClass();
    }

    @Override
    public Integer getIndex() {
        return null;
    }

    @Override
    public boolean isHasTableAlias() {
        return this.hasTableAlias;
    }

    @Override
    public String getTableAlias() {
        return this.tableAlias;
    }

    @Override
    public boolean isPk() {
        return false;
    }

    @Override
    public String getColumn() {
        return this.cache.field.getName();
    }

    @Override
    public Class<?> getColumnType() {
        return this.cache.getType();
    }

    @Override
    public String getTagColumn() {
        return this.cache.field.getName();
    }

    @Override
    public String getColumProperty() {
        return this.cache.field.getName();
    }

    @Override
    public boolean hasTypeHandle() {
        return false;
    }

    @Override
    public TypeHandler<?> getTypeHandle() {
        return null;
    }

    @Override
    public boolean isHasAlias() {
        return StringUtils.isNotBlank(this.alias);
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public TableFieldInfo getTableFieldInfo() {
        return null;
    }

    @Override
    public boolean isFunc() {
        return false;
    }

    @Override
    public SelectFunc.Arg[] getArgs() {
        return null;
    }

    @Override
    public BaseFuncEnum getFunc() {
        return null;
    }

    @Override
    public boolean isLabel() {
        return false;
    }

    @Override
    public boolean isStr() {
        return false;
    }
}
