package com.github.leheyue.test.join.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Accessors(chain = true)
@TableName("address")
public class AddressDO extends AddressGeneric<Integer, Integer, Integer, String, String, Boolean> implements Serializable {

    @TableField(exist = false)
    private boolean aaa;

}
