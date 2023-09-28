package com.leheyue.test.springboot3jdk17.mapper;

import com.leheyue.test.springboot3jdk17.entity.AddressDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@SuppressWarnings("unused")
public interface AddressMapper extends MyBaseMapper<AddressDO> {
}
