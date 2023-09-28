package com.leheyue.test.springboot3jdk17.mapper;

import com.github.leheyue.base.MPJBaseMapper;
import com.leheyue.test.springboot3jdk17.entity.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDTOMapper extends MPJBaseMapper<UserDto> {
}
