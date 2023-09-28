package com.github.leheyue.test.collection.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("table_c")
public class TableC {

    @TableId
    private Integer id;

    private Integer bid;

    private String name;
}
