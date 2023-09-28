package com.github.leheyue.test.join.mapper;

import com.github.leheyue.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyBaseMapper<T> extends MPJBaseMapper<T> {

    @SuppressWarnings("UnusedReturnValue")
    int insertBatchSomeColumn(List<T> entityList);
}
