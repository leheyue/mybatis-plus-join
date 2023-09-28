package com.github.leheyue.test.kt.service.impl

import com.github.leheyue.base.MPJBaseServiceImpl
import com.github.leheyue.test.kt.entity.UserDO
import com.github.leheyue.test.kt.mapper.UserMapper
import com.github.leheyue.test.kt.service.UserService
import org.springframework.stereotype.Service

@Service
open class UserServiceImpl : MPJBaseServiceImpl<UserMapper?, UserDO?>(), UserService
