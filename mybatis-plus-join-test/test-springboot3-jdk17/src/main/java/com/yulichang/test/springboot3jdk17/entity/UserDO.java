package com.leheyue.test.springboot3jdk17.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.leheyue.test.springboot3jdk17.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@TableName(value = "`user`", autoResultMap = true)
public class UserDO implements Serializable {

    @TableId
    private Integer id;

    private Integer pid;

    @TableField("`name`")
    private String name;

    @TableField(value = "`json`", typeHandler = JacksonTypeHandler.class)
    private Map<String, String> json;

    private Sex sex;

    @TableField("head_img")
    private String img;

    private LocalDateTime createTime;

    private Integer addressId;

    private Integer addressId2;

    @TableLogic
    private Boolean del;

    private Integer createBy;

    @TableField(exist = false)
    private String createName;

    private Integer updateBy;

    @TableField(exist = false)
    private String updateName;


    @TableField(exist = false)
    private String alias;

    @TableField(exist = false)
    private List<UserDO> children;

    @TableField(exist = false)
    private List<AddressDO> addressList;

    @TableField(exist = false)
    private List<AddressDO> addressList2;
}
