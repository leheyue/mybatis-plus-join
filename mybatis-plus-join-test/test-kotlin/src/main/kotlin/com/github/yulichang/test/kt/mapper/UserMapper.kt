package com.github.leheyue.test.kt.mapper

import com.github.leheyue.test.kt.entity.UserDO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserMapper : MyBaseMapper<UserDO?>
