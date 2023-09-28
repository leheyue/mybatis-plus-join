package com.github.leheyue.query;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.leheyue.config.ConfigProperties;
import com.github.leheyue.query.interfaces.StringJoin;
import com.github.leheyue.toolkit.Asserts;
import com.github.leheyue.toolkit.TableHelper;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * MPJLambdaQueryWrapper
 * 参考 -> {@link com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper}
 *
 * @author yulichang
 * @see com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 */
@SuppressWarnings("unused")
public class MPJLambdaQueryWrapper<T> extends AbstractLambdaWrapper<T, MPJLambdaQueryWrapper<T>>
        implements Query<MPJLambdaQueryWrapper<T>, T, SFunction<T, ?>>, StringJoin<MPJLambdaQueryWrapper<T>, T> {

    /**
     * 查询字段
     */
    private SharedString sqlSelect = new SharedString();

    /**
     * 连表字段
     */
    private SharedString from = SharedString.emptyString();

    /**
     * 主表别名
     */
    private final String alias = ConfigProperties.tableAlias;

    /**
     * 查询的列
     */
    private List<String> selectColumns = new ArrayList<>();

    /**
     * 排除的字段
     */
    private List<String> ignoreColumns = new ArrayList<>();

    /**
     * 是否 select distinct
     */
    private boolean selectDistinct = false;

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public MPJLambdaQueryWrapper() {
        super.initNeed();
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(...)
     */
    MPJLambdaQueryWrapper(T entity, Class<T> entityClass, SharedString from, SharedString sqlSelect, AtomicInteger paramNameSeq,
                          Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                          SharedString lastSql, SharedString sqlComment, SharedString sqlFirst,
                          List<String> selectColumns, List<String> ignoreColumns, boolean selectDistinct) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.from = from;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
        this.selectColumns = selectColumns;
        this.ignoreColumns = ignoreColumns;
        this.selectDistinct = selectDistinct;
    }

    /**
     * SELECT 部分 SQL 设置
     *
     * @param columns 查询字段
     */
    @SafeVarargs
    public final MPJLambdaQueryWrapper<T> select(SFunction<T, ?>... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            for (SFunction<T, ?> s : columns) {
                selectColumns.add(columnToString(s, false));
            }
        }
        return typedThis;
    }

    @Override
    public MPJLambdaQueryWrapper<T> select(boolean condition, List<SFunction<T, ?>> columns) {
        if (condition && CollectionUtils.isNotEmpty(columns)) {
            for (SFunction<T, ?> s : columns) {
                selectColumns.add(columnToString(s, false));
            }
        }
        return typedThis;
    }

    /**
     * 忽略查询字段
     * <p>
     * 用法: selectIgnore("t.id","t.sex","a.area")
     *
     * @since 1.1.3
     */
    public MPJLambdaQueryWrapper<T> selectIgnore(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            ignoreColumns.addAll(Arrays.asList(columns));
        }
        return typedThis;
    }

    /**
     * 忽略查询字段
     * <p>
     * 用法: selectIgnore("t.id","t.sex","a.area")
     *
     * @since 1.1.3
     */
    @SafeVarargs
    public final MPJLambdaQueryWrapper<T> selectIgnore(SFunction<T, ?>... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            for (SFunction<T, ?> s : columns) {
                ignoreColumns.add(alias + StringPool.DOT + columnToString(s));
            }
        }
        return typedThis;
    }

    @Override
    protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
        return alias + StringPool.DOT + super.columnToString(column, onlyColumn);
    }

    public MPJLambdaQueryWrapper<T> select(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            Collections.addAll(selectColumns, columns);
        }
        return typedThis;
    }

    /**
     * 只针对主表
     * <p>
     * 过滤查询的字段信息(主键除外!)
     * <p>例1: 只要 java 字段名以 "test" 开头的             -> select(i -&gt; i.getProperty().startsWith("test"))</p>
     * <p>例2: 只要 java 字段属性是 CharSequence 类型的     -> select(TableFieldInfo::isCharSequence)</p>
     * <p>例3: 只要 java 字段没有填充策略的                 -> select(i -&gt; i.getFieldFill() == FieldFill.DEFAULT)</p>
     * <p>例4: 要全部字段                                   -> select(i -&gt; true)</p>
     * <p>例5: 只要主键字段                                 -> select(i -&gt; false)</p>
     *
     * @param predicate 过滤方式
     * @return this
     */
    @Override
    public MPJLambdaQueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        TableInfo info = TableHelper.get(entityClass);
        Asserts.hasTable(info, entityClass);
        selectColumns.addAll(info.getFieldList().stream().filter(predicate).map(c ->
                alias + StringPool.DOT + c.getColumn()).collect(Collectors.toList()));
        return typedThis;
    }


    /**
     * 查询主表全部字段
     *
     * @param clazz 主表class
     */
    public final MPJLambdaQueryWrapper<T> selectAll(Class<T> clazz) {
        return selectAll(clazz, alias);
    }

    /**
     * 查询表全部字段
     *
     * @param clazz 表实体
     * @param as    表别名
     */
    @SuppressWarnings("DuplicatedCode")
    public final MPJLambdaQueryWrapper<T> selectAll(Class<?> clazz, String as) {
        TableInfo info = TableHelper.get(clazz);
        Asserts.hasTable(info, clazz);
        if (info.havePK()) {
            selectColumns.add(as + StringPool.DOT + info.getKeyColumn());
        }
        selectColumns.addAll(info.getFieldList().stream().map(i ->
                as + StringPool.DOT + i.getColumn()).collect(Collectors.toList()));
        return typedThis;
    }

    /**
     * 返回一个支持 lambda 函数写法的 wrapper
     */
    public MPJQueryWrapper<T> stringQuery() {
        return new MPJQueryWrapper<>(getEntity(), getEntityClass(), paramNameSeq, paramNameValuePairs,
                expression, sqlSelect, from, lastSql, sqlComment, sqlFirst, selectColumns, ignoreColumns, selectDistinct);
    }

    @Override
    public String getSqlSelect() {
        if (StringUtils.isBlank(sqlSelect.getStringValue())) {
            if (CollectionUtils.isNotEmpty(ignoreColumns)) {
                selectColumns.removeIf(ignoreColumns::contains);
            }
            sqlSelect.setStringValue(String.join(StringPool.COMMA, selectColumns));
        }
        return sqlSelect.getStringValue();
    }

    /**
     * sql去重
     * select distinct
     */
    public MPJLambdaQueryWrapper<T> distinct() {
        this.selectDistinct = true;
        return typedThis;
    }

    public String getFrom() {
        return from.getStringValue();
    }


    public String getAlias() {
        return alias;
    }

    public boolean getSelectDistinct() {
        return selectDistinct;
    }

    /**
     * 用于生成嵌套 sql
     * <p>故 sqlSelect selectColumn ignoreColumns from不向下传递</p>
     */
    @Override
    protected MPJLambdaQueryWrapper<T> instance() {
        return new MPJLambdaQueryWrapper<>(getEntity(), getEntityClass(), null, null, paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(), null, null, selectDistinct);
    }

    @Override
    public void clear() {
        super.clear();
        sqlSelect.toNull();
        from.toNull();
        selectColumns.clear();
        ignoreColumns.clear();
    }

    @Override
    public MPJLambdaQueryWrapper<T> join(String keyWord, boolean condition, String joinSql) {
        if (condition) {
            from.setStringValue(from.getStringValue() + keyWord + joinSql);
        }
        return typedThis;
    }
}
