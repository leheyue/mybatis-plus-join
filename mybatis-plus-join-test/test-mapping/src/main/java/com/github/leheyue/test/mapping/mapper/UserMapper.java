package com.github.leheyue.test.mapping.mapper;

import com.github.leheyue.base.MPJBaseMapper;
import com.github.leheyue.test.mapping.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends MPJBaseMapper<UserDO> {

}
