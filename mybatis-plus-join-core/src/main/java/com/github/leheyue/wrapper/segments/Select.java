package com.github.leheyue.wrapper.segments;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.github.leheyue.wrapper.enums.BaseFuncEnum;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;

/**
 * 查询列
 *
 * @author yulichang
 * @since 1.3.10
 */
public interface Select extends Serializable {

    Class<?> getClazz();

    Integer getIndex();

    boolean isHasTableAlias();

    String getTableAlias();

    boolean isPk();

    String getColumn();

    Class<?> getColumnType();

    String getTagColumn();

    String getColumProperty();

    boolean hasTypeHandle();

    TypeHandler<?> getTypeHandle();

    boolean isHasAlias();

    String getAlias();

    TableFieldInfo getTableFieldInfo();

    boolean isFunc();

    SelectFunc.Arg[] getArgs();

    BaseFuncEnum getFunc();

    boolean isLabel();

    boolean isStr();
}
