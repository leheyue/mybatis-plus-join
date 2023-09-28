package com.github.leheyue.test.join.service.impl;

import com.github.leheyue.base.MPJBaseServiceImpl;
import com.github.leheyue.test.join.entity.UserDO;
import com.github.leheyue.test.join.mapper.UserMapper;
import com.github.leheyue.test.join.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends MPJBaseServiceImpl<UserMapper, UserDO> implements UserService {


}
