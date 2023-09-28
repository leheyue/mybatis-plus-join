package com.github.leheyue.test.kt.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler
import com.github.leheyue.annotation.DynamicTableName
import com.github.leheyue.test.kt.enums.Sex
import lombok.experimental.FieldNameConstants
import java.io.Serializable
import java.time.LocalDateTime

@Suppress("unused")
@DynamicTableName
@FieldNameConstants
@TableName(value = "`user`", autoResultMap = true)
open class UserDO : ID<Int?>(), Serializable {
    var pid: Int? = null

    @TableField("`name`")
    var name: String? = null

    @TableField(value = "`json`", typeHandler = JacksonTypeHandler::class)
    var json: Map<String, String>? = null
    var sex: Sex? = null

    @TableField("head_img")
    var img: String? = null
    var createTime: LocalDateTime? = null
    var addressId: Int? = null
    var addressId2: Int? = null

    @TableLogic
    var del: Boolean? = null
    var createBy: Int? = null

    @TableField(exist = false)
    var createName: String? = null
    var updateBy: Int? = null

    @TableField(exist = false)
    var updateName: String? = null

    @TableField(exist = false)
    var alias: String? = null

    @TableField(exist = false)
    var children: List<UserDO>? = null

    @TableField(exist = false)
    var addressList: List<AddressDO>? = null

    @TableField(exist = false)
    var addressList2: List<AddressDO>? = null
}
