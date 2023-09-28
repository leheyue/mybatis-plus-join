package com.github.leheyue.test.join.mapper;

import com.github.leheyue.test.join.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends MyBaseMapper<UserDO> {

}
