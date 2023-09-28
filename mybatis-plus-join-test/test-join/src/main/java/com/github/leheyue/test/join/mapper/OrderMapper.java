package com.github.leheyue.test.join.mapper;

import com.github.leheyue.test.join.entity.OrderDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends MyBaseMapper<OrderDO> {

}
