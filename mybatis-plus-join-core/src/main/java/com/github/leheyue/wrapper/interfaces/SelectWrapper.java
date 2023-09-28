package com.github.leheyue.wrapper.interfaces;

import com.github.leheyue.wrapper.segments.Select;

import java.util.List;

/**
 * @author yulichang
 * @since 1.4.6
 */
public interface SelectWrapper<Entity, Children> {

    Class<Entity> getEntityClass();

    Children setEntityClass(Class<Entity> clazz);

    List<Select> getSelectColumns();

    Children selectAll(Class<?> clazz);

    boolean isResultMap();

    List<?> getResultMapMybatisLabel();
}
