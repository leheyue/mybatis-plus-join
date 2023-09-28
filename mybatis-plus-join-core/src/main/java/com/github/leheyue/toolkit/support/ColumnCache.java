package com.github.leheyue.toolkit.support;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.github.leheyue.config.ConfigProperties;
import com.github.leheyue.toolkit.TableHelper;
import com.github.leheyue.wrapper.segments.SelectCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * select缓存
 *
 * @author yulichang
 * @since 1.3.10
 */
public class ColumnCache {

    private static final Map<Class<?>, List<SelectCache>> LIST_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Map<String, SelectCache>> MAP_CACHE = new ConcurrentHashMap<>();

    public static List<SelectCache> getListField(Class<?> clazz) {
        return LIST_CACHE.computeIfAbsent(clazz, c -> {
            TableInfo tableInfo = TableHelper.get(clazz);
            List<SelectCache> list = new ArrayList<>();
            if (Objects.isNull(tableInfo)) {
                return list;
            }
            if (ConfigProperties.tableInfoAdapter.mpjHasPK(tableInfo)) {
                list.add(new SelectCache(clazz, true, tableInfo.getKeyColumn(), tableInfo.getKeyType(), tableInfo.getKeyProperty(), null));
            }
            list.addAll(tableInfo.getFieldList().stream().map(f -> new SelectCache(clazz, false, f.getColumn(), f.getPropertyType(), f.getProperty(), f)).collect(Collectors.toList()));
            return list;
        });
    }

    public static Map<String, SelectCache> getMapField(Class<?> clazz) {
        return MAP_CACHE.computeIfAbsent(clazz, c -> getListField(c).stream().collect(Collectors.toMap(SelectCache::getColumProperty, Function.identity(), (i, j) -> j)));
    }
}
