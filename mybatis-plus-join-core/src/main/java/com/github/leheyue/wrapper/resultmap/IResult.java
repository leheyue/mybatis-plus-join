package com.github.leheyue.wrapper.resultmap;

import com.github.leheyue.wrapper.segments.SelectCache;
import org.apache.ibatis.type.JdbcType;

public interface IResult {

    boolean isId();

    String getIndex();

    SelectCache getSelectNormal();

    String getProperty();

    Class<?> getJavaType();

    JdbcType getJdbcType();
}
