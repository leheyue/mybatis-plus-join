package com.github.leheyue.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * copy {@link com.baomidou.mybatisplus.core.injector.methods.SelectCount}
 *
 * @author yulichang
 * @since 1.1.8
 */
public class SelectJoinCount extends MPJAbstractMethod {

    @SuppressWarnings("deprecation")
    public SelectJoinCount() {
        super();
    }

    @SuppressWarnings("unused")
    public SelectJoinCount(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.SELECT_JOIN_COUNT;
        String sql = String.format(sqlMethod.getSql(), sqlFirst(), sqlCount(),
                mpjTableName(tableInfo), sqlAlias(), sqlFrom(), sqlWhereEntityWrapper(true, tableInfo), sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForOther(mapperClass, sqlMethod.getMethod(), sqlSource, Long.class);
    }
}
