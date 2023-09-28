package com.github.leheyue.extension.mapping.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.leheyue.annotation.EntityMapping;
import com.github.leheyue.annotation.FieldMapping;
import com.github.leheyue.extension.mapping.config.DeepConfig;
import com.github.leheyue.extension.mapping.relation.Relation;

import java.util.function.Function;

/**
 * 深度查询
 * <p>
 * 对配置了映射注解的字段进行查询
 * 目前查询深度只支持2级(只解析当前实体类的映射注解,不会对查询结果再次解析注解)
 * 多级查询可能存在循环引用的问题，也可能会导致全量查询
 * 用于替换deep
 *
 * @author yulichang
 * @see EntityMapping
 * @see FieldMapping
 * @since 1.4.4
 */
@SuppressWarnings({"unchecked", "unused"})
public interface MPJRelationService<T> extends IService<T> {

    /**
     * 通过注解实现单表多次查询
     *
     * @param function BaseMapper调用方法
     * @see com.github.leheyue.annotation.EntityMapping
     * @see com.github.leheyue.annotation.FieldMapping
     */
    default <R, M extends BaseMapper<T>> R getRelation(Function<M, R> function) {
        return Relation.mpjGetRelation(function.apply((M) getBaseMapper()), DeepConfig.defaultConfig());
    }

    /**
     * 通过注解实现单表多次查询
     *
     * @param function BaseMapper调用方法
     * @see com.github.leheyue.annotation.EntityMapping
     * @see com.github.leheyue.annotation.FieldMapping
     */
    default <R, M extends BaseMapper<T>> R getRelation(Function<M, R> function, DeepConfig<T> config) {
        return Relation.mpjGetRelation(function.apply((M) getBaseMapper()), config);
    }

    /**
     * 通过注解实现单表多次查询
     *
     * @param function BaseMapper调用方法
     * @param config   映射配置
     * @see com.github.leheyue.annotation.EntityMapping
     * @see com.github.leheyue.annotation.FieldMapping
     */
    default <R, M extends BaseMapper<T>> R getRelation(Function<M, R> function, Function<DeepConfig.Builder<T>, DeepConfig.Builder<T>> config) {
        return Relation.mpjGetRelation(function.apply((M) getBaseMapper()), config.apply(DeepConfig.builder()).build());
    }
}
