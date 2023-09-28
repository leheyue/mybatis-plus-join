package com.github.leheyue.test.join.mapper;

import com.github.leheyue.base.MPJBaseMapper;
import com.github.leheyue.test.join.entity.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDTOMapper extends MPJBaseMapper<UserDto> {
}
