package com.github.leheyue.test.kt.mapper

import com.github.leheyue.base.MPJBaseMapper
import com.github.leheyue.test.kt.entity.UserDto
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserDTOMapper : MPJBaseMapper<UserDto?>
