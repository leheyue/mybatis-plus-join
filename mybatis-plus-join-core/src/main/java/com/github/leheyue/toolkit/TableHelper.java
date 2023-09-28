package com.github.leheyue.toolkit;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yulichang
 * @since 1.4.3
 */
public class TableHelper {

    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    public static void init(Class<?> newClass, Class<?> oldClass) {
        if (Objects.nonNull(newClass)) {
            TableInfo info = TableInfoHelper.getTableInfo(newClass);
            if (Objects.isNull(info)) {
                if (Objects.nonNull(oldClass)) {
                    TableInfo oldInfo = TableInfoHelper.getTableInfo(oldClass);
                    if (Objects.nonNull(oldInfo)) {
                        TABLE_INFO_CACHE.put(newClass, oldInfo);
                    }
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static TableInfo get(Class<?> clazz) {
        if (Objects.nonNull(clazz)) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
            if (Objects.nonNull(tableInfo)) {
                return tableInfo;
            }
            TableInfo info = TABLE_INFO_CACHE.get(clazz);
            //尝试获取父类缓存
            Class<?> currentClass = clazz;
            while (Object.class != currentClass) {
                currentClass = currentClass.getSuperclass();
                info = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
            }
            if (Objects.nonNull(info)) {
                TABLE_INFO_CACHE.put(currentClass, info);
            }
            return info;
        } else {
            return null;
        }
    }
}
