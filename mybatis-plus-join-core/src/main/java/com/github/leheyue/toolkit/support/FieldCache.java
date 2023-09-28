package com.github.leheyue.toolkit.support;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * 反射字段缓存
 *
 * @author yulichang
 * @since 1.4.5
 */
@Data
public class FieldCache {

    public Field field;

    public Class<?> type;
}
