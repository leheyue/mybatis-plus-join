package com.github.leheyue.test.kt.mapper

import com.github.leheyue.test.kt.entity.AddressDO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface AddressMapper : MyBaseMapper<AddressDO?>
