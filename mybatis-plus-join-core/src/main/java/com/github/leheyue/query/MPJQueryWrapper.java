package com.github.leheyue.query;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.github.leheyue.config.ConfigProperties;
import com.github.leheyue.query.interfaces.StringJoin;
import com.github.leheyue.toolkit.Asserts;
import com.github.leheyue.toolkit.MPJSqlInjectionUtils;
import com.github.leheyue.toolkit.TableHelper;
import com.github.leheyue.wrapper.interfaces.Chain;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * copy {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
 * 推荐使用 JoinWrappers.<UserDO>queryJoin();构造
 *
 * @author yulichang
 * @see com.github.leheyue.toolkit.JoinWrappers
 */
@SuppressWarnings("unused")
public class MPJQueryWrapper<T> extends AbstractWrapper<T, String, MPJQueryWrapper<T>> implements
        Query<MPJQueryWrapper<T>, T, String>, StringJoin<MPJQueryWrapper<T>, T>, Chain<T> {

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
    @Getter
    private String alias = ConfigProperties.tableAlias;

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
     * 主表逻辑删除
     */
    private boolean logicSql = true;

    /**
     * 动态表名
     */
    private Function<String, String> tableNameFunc;

    /**
     * 检查 SQL 注入过滤
     */
    private boolean checkSqlInjection = false;


    public MPJQueryWrapper() {
        super.initNeed();
    }

    public MPJQueryWrapper(Class<T> clazz) {
        super.setEntityClass(clazz);
        super.initNeed();
    }

    public MPJQueryWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
    }

    /**
     * 非对外公开的构造方法,只用于生产嵌套 sql
     *
     * @param entityClass 本不应该需要的
     */
    public MPJQueryWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq,
                           Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                           SharedString sqlSelect, SharedString from, SharedString lastSql,
                           SharedString sqlComment, SharedString sqlFirst,
                           List<String> selectColumns, List<String> ignoreColumns, boolean selectDistinct) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.lastSql = lastSql;
        this.from = from;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
        this.selectColumns = selectColumns;
        this.ignoreColumns = ignoreColumns;
        this.selectDistinct = selectDistinct;
    }

    /**
     * 开启检查 SQL 注入
     */
    public MPJQueryWrapper<T> checkSqlInjection() {
        this.checkSqlInjection = true;
        return this;
    }

    @Override
    protected String columnToString(String column) {
        if (checkSqlInjection && MPJSqlInjectionUtils.check(column)) {
            throw new MybatisPlusException("Discovering SQL injection column: " + column);
        }
        return column;
    }

    /**
     * sql去重
     * select distinct
     */
    public MPJQueryWrapper<T> distinct() {
        this.selectDistinct = true;
        return typedThis;
    }

    @Override
    public MPJQueryWrapper<T> select(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            selectColumns.addAll(Arrays.asList(columns));
        }
        return typedThis;
    }

    @Override
    public MPJQueryWrapper<T> select(boolean condition, List<String> columns) {
        if (condition && CollectionUtils.isNotEmpty(columns)) {
            selectColumns.addAll(columns);
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
    public MPJQueryWrapper<T> selectIgnore(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            ignoreColumns.addAll(Arrays.asList(columns));
        }
        return typedThis;
    }

    /**
     * 此方法只能用于主表
     * 不含主键
     *
     * @param entityClass 主表class
     * @param predicate   条件lambda
     */
    @Override
    public MPJQueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        TableInfo info = TableHelper.get(entityClass);
        Asserts.hasTable(info, entityClass);
        selectColumns.addAll(info.getFieldList().stream().filter(predicate).map(c ->
                alias + StringPool.DOT + c.getSqlSelect()).collect(Collectors.toList()));
        return typedThis;
    }


    /**
     * 查询主表全部字段
     *
     * @param clazz 主表class
     */
    public final MPJQueryWrapper<T> selectAll(Class<T> clazz) {
        selectAll(clazz, alias);
        return typedThis;
    }

    /**
     * 查询指定实体全部字段
     *
     * @param as 实体对应的别名
     */
    @SuppressWarnings({"DuplicatedCode", "UnusedReturnValue"})
    public final MPJQueryWrapper<T> selectAll(Class<?> clazz, String as) {
        TableInfo info = TableHelper.get(clazz);
        Asserts.hasTable(info, clazz);
        if (ConfigProperties.tableInfoAdapter.mpjHasPK(info)) {
            selectColumns.add(as + StringPool.DOT + info.getKeySqlSelect());
        }
        selectColumns.addAll(info.getFieldList().stream().map(i ->
                as + StringPool.DOT + i.getSqlSelect()).collect(Collectors.toList()));
        return typedThis;
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

    public boolean getSelectDistinct() {
        return selectDistinct;
    }

    public String getFrom() {
        return from.getStringValue();
    }

    /**
     * 设置主表别名
     * 如果要用，请最先调用，
     * <pre>
     * 正例  new QueryWrapper().setAlias("a").selectAll(UserDO.class)....
     * 反例  new QueryWrapper().selectAll(UserDO.class).setAlias("a")....
     * <pre/>
     *
     * @param alias 主表别名
     */
    public MPJQueryWrapper<T> setAlias(String alias) {
        Assert.isTrue(StringUtils.isNotBlank(alias), "别名不能为空");
        this.alias = alias;
        return this;
    }

    /**
     * 逻辑删除
     */
    public String getSubLogicSql() {
        return StringPool.EMPTY;
    }

    /**
     * 关闭主表逻辑删除
     */
    public MPJQueryWrapper<T> disableLogicDel() {
        this.logicSql = false;
        return typedThis;
    }

    /**
     * 启用主表逻辑删除
     */
    public MPJQueryWrapper<T> enableLogicDel() {
        this.logicSql = true;
        return typedThis;
    }

    /**
     * 逻辑删除
     */
    public boolean getLogicSql() {
        return logicSql;
    }

    /**
     * 动态表名
     * 如果主表需要动态表名,主表实体必须添加 @DynamicTableName 注解
     * 关联表则不需要 加不加注解都会生效
     * <p>
     *
     * @see com.github.leheyue.annotation.DynamicTableName
     */
    public MPJQueryWrapper<T> setTableName(Function<String, String> func) {
        this.tableNameFunc = func;
        return typedThis;
    }

    public String getTableName(String tableName) {
        if (this.tableNameFunc == null) {
            return tableName;
        }
        return this.tableNameFunc.apply(tableName);
    }

    public String getTableNameEnc(String tableName) {
        String decode;
        try {
            decode = URLDecoder.decode(tableName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            decode = tableName;
        }
        if (this.tableNameFunc == null) {
            return decode;
        }
        return this.tableNameFunc.apply(decode);
    }

    /**
     * 返回一个支持 lambda 函数写法的 wrapper
     */
    public MPJLambdaQueryWrapper<T> lambda() {
        return new MPJLambdaQueryWrapper<>(getEntity(), getEntityClass(), from, sqlSelect, paramNameSeq, paramNameValuePairs,
                expression, lastSql, sqlComment, sqlFirst, selectColumns, ignoreColumns, selectDistinct);
    }

    /**
     * 用于生成嵌套 sql
     * <p>故 sqlSelect selectColumn ignoreColumns from不向下传递</p>
     */
    @Override
    protected MPJQueryWrapper<T> instance() {
        return new MPJQueryWrapper<>(getEntity(), getEntityClass(), paramNameSeq, paramNameValuePairs, new MergeSegments(),
                null, null, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(), null, null, selectDistinct);
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
    public MPJQueryWrapper<T> join(String keyWord, boolean condition, String joinSql) {
        if (condition) {
            from.setStringValue(from.getStringValue() + StringPool.SPACE + keyWord + StringPool.SPACE + joinSql);
        }
        return typedThis;
    }
}
