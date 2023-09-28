package com.github.leheyue.config;

import com.github.leheyue.adapter.AdapterHelper;
import com.github.leheyue.adapter.base.ITableInfoAdapter;
import com.github.leheyue.config.enums.LogicDelTypeEnum;

/**
 * @author yulichang
 * @since 1.3.7
 */
public class ConfigProperties {

    /**
     * 是否开启副表逻辑删除
     */
    public static boolean subTableLogic = true;
    /**
     * 是否开启 ms 缓存
     */
    public static boolean msCache = true;
    /**
     * 表别名
     */
    public static String tableAlias = "t";
    /**
     * 字段名重复时前缀
     */
    public static String joinPrefix = "join";
    /**
     * 逻辑删除类型 支持 where on
     */
    public static LogicDelTypeEnum logicDelType = LogicDelTypeEnum.WHERE;
    /**
     * 逻辑删除类型 支持 where on
     */
    public static int mappingMaxCount = 5;
    /**
     * TableInfo适配器
     */
    public static ITableInfoAdapter tableInfoAdapter = AdapterHelper.getTableInfoAdapter();
    /**
     * 子查询别名
     */
    public static String subQueryAlias = "st";
}
