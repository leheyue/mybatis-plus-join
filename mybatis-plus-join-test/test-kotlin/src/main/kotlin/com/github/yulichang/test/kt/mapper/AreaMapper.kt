package com.github.leheyue.test.kt.mapper

import com.github.leheyue.test.kt.entity.AreaDO
import org.apache.ibatis.annotations.Mapper

@Suppress("unused")
@Mapper
interface AreaMapper : MyBaseMapper<AreaDO?>
