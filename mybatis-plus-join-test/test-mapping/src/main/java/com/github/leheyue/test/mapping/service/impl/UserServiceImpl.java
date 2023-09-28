package com.github.leheyue.test.mapping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.leheyue.extension.mapping.base.MPJDeepService;
import com.github.leheyue.test.mapping.entity.UserDO;
import com.github.leheyue.test.mapping.mapper.UserMapper;
import com.github.leheyue.test.mapping.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService, MPJDeepService<UserDO> {
}
