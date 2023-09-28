package com.github.leheyue.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.github.leheyue.adapter.v352.AbstractMethod352;
import com.github.leheyue.config.ConfigProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yulichang
 * @see AbstractMethod
 */
public abstract class MPJAbstractMethod extends AbstractMethod352 implements MPJBaseMethod {


    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public MPJAbstractMethod() {
        super();
    }

    @SuppressWarnings({"unused"})
    protected MPJAbstractMethod(String methodName) {
        super(methodName);
    }

    /**
     * 连表操作不考虑entity查询和逻辑删除
     */
    @Override
    protected String sqlWhereEntityWrapper(boolean newLine, TableInfo table) {
        return mpjSqlWhereEntityWrapper(newLine, table);
    }

    @Override
    protected String sqlSelectColumns(TableInfo table, boolean queryWrapper) {
        String selectColumns = ASTERISK;
        if (table.getResultMap() == null || (table.getResultMap() != null)) {
            selectColumns = table.getAllSqlSelect();
            String[] columns = selectColumns.split(StringPool.COMMA);
            List<String> selectColumnList = new ArrayList<>();
            for (String c : columns) {
                selectColumnList.add(ConfigProperties.tableAlias + StringPool.DOT + c);
            }
            selectColumns = String.join(StringPool.COMMA, selectColumnList);
        }
        if (!queryWrapper) {
            return selectColumns;
        }
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null", WRAPPER, Q_WRAPPER_SQL_SELECT),
                SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), selectColumns);
    }

    @Override
    protected String sqlCount() {
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null and %s != ''", WRAPPER,
                        Q_WRAPPER_SQL_SELECT, Q_WRAPPER_SQL_SELECT),
                SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), ASTERISK);
    }

    protected String sqlAlias() {
        return SqlScriptUtils.convertIf("${ew.alias}", String.format("%s != null and %s != ''", "ew.alias", "ew.alias"), false);
    }

    protected String sqlFrom() {
        return SqlScriptUtils.convertIf("${ew.from}", String.format("%s != null and %s != ''", "ew.from", "ew.from"), false);
    }

    protected String sqlDistinct() {
        return SqlScriptUtils.convertIf("DISTINCT", "ew.selectDistinct", false);
    }

    @Override
    protected String sqlFirst() {
        try {
            return super.sqlFirst();
        } catch (Throwable e) {
            return "";
        }
    }
}
