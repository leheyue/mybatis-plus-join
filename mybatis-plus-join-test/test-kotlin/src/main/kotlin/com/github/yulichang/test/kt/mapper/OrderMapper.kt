package com.github.leheyue.test.kt.mapper

import com.github.leheyue.test.kt.entity.OrderDO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface OrderMapper : MyBaseMapper<OrderDO?>
