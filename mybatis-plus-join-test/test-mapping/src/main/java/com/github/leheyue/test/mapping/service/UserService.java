package com.github.leheyue.test.mapping.service;

import com.github.leheyue.extension.mapping.base.MPJDeepService;
import com.github.leheyue.extension.mapping.base.MPJRelationService;
import com.github.leheyue.test.mapping.entity.UserDO;

public interface UserService extends MPJDeepService<UserDO>, MPJRelationService<UserDO> {
}
