package com.leheyue.test.springboot3jdk17.mapper;

import com.github.leheyue.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.List;

@Mapper
@SuppressWarnings("unused")
public interface MyBaseMapper<T extends Serializable> extends MPJBaseMapper<T> {

    int insertBatchSomeColumn(List<T> entityList);
}
