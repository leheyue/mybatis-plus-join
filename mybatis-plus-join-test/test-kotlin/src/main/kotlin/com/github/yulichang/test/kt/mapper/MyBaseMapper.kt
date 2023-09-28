package com.github.leheyue.test.kt.mapper

import com.github.leheyue.base.MPJBaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface MyBaseMapper<T> : MPJBaseMapper<T> {
    fun insertBatchSomeColumn(entityList: List<T>?): Int
}
