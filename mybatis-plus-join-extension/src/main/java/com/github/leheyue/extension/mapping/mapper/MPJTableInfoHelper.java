package com.github.leheyue.extension.mapping.mapper;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.github.leheyue.annotation.EntityMapping;
import com.github.leheyue.annotation.FieldMapping;
import com.github.leheyue.toolkit.MPJReflectionKit;
import com.github.leheyue.toolkit.TableHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拷贝 {@link TableInfoHelper}
 *
 * @author yulichang
 * @see TableInfoHelper
 */
public class MPJTableInfoHelper {

    /**
     * 储存反射类表信息
     */
    private static final Map<Class<?>, MPJTableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * <p>
     * 获取实体映射表信息
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public static MPJTableInfo getTableInfo(Class<?> clazz) {
        if (clazz == null || MPJReflectionKit.isPrimitiveOrWrapper(clazz) || clazz == String.class || clazz.isInterface()) {
            return null;
        }
        return TABLE_INFO_CACHE.get(clazz);
    }

    /**
     * <p>
     * 获取所有实体映射表信息
     * </p>
     *
     * @return 数据库表反射信息集合
     */
    public static List<MPJTableInfo> getTableInfos() {
        return Collections.unmodifiableList(new ArrayList<>(TABLE_INFO_CACHE.values()));
    }

    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * </p>
     *
     * @param clazz 反射实体类
     */
    public synchronized static void initTableInfo(Class<?> clazz, Class<?> mapperClass) {
        MPJTableInfo info = TABLE_INFO_CACHE.get(clazz);
        if (info != null) {
            return;
        }
        MPJTableInfo mpjTableInfo = new MPJTableInfo();
        mpjTableInfo.setMapperClass(mapperClass);
        mpjTableInfo.setEntityClass(clazz);
        TableInfo tableInfo = TableHelper.get(clazz);
        if (tableInfo == null) {
            if (mapperClass != null) {
                return;
            }
        }
        mpjTableInfo.setDto(tableInfo == null);
        mpjTableInfo.setTableInfo(tableInfo);
        initMapping(mpjTableInfo);
        TABLE_INFO_CACHE.put(clazz, mpjTableInfo);
    }

    private static boolean isExistMapping(Class<?> clazz) {
        return ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz)).stream().anyMatch(field -> field.isAnnotationPresent(EntityMapping.class));
    }

    private static boolean isExistMappingField(Class<?> clazz) {
        return ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz)).stream().anyMatch(field -> field.isAnnotationPresent(FieldMapping.class));
    }

    /**
     * 初始化映射相关
     */
    public static void initMapping(MPJTableInfo mpjTableInfo) {
        // 是否存在 @EntityMapping 注解
        boolean existMapping = isExistMapping(mpjTableInfo.getEntityClass());
        mpjTableInfo.setHasMapping(existMapping);
        // 是否存在 @FieldMapping 注解
        boolean existMappingField = isExistMappingField(mpjTableInfo.getEntityClass());
        mpjTableInfo.setHasMappingField(existMappingField);
        mpjTableInfo.setHasMappingOrField(existMapping || existMappingField);
        /* 关系映射初始化 */
        List<MPJTableFieldInfo> mpjFieldList = new ArrayList<>();
        List<Field> fields = ReflectionKit.getFieldList(ClassUtils.getUserClass(mpjTableInfo.getEntityClass()));
        for (Field field : fields) {
            if (existMapping) {
                EntityMapping mapping = field.getAnnotation(EntityMapping.class);
                if (mapping != null) {
                    mpjFieldList.add(new MPJTableFieldInfo(mpjTableInfo.getEntityClass(), mapping, field));
                }
            }
            if (existMappingField) {
                FieldMapping mapping = field.getAnnotation(FieldMapping.class);
                if (mapping != null) {
                    mpjFieldList.add(new MPJTableFieldInfo(mpjTableInfo.getEntityClass(), mapping, field));
                }
            }
        }
        /* 映射字段列表 */
        mpjTableInfo.setFieldList(mpjFieldList);
    }


}
