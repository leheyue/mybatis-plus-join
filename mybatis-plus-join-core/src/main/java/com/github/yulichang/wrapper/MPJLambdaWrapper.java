package com.github.yulichang.wrapper;

import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.config.ConfigProperties;
import com.github.yulichang.toolkit.LambdaUtils;
import com.github.yulichang.toolkit.*;
import com.github.yulichang.toolkit.support.ColumnCache;
import com.github.yulichang.toolkit.support.FieldCache;
import com.github.yulichang.wrapper.interfaces.Chain;
import com.github.yulichang.wrapper.interfaces.Query;
import com.github.yulichang.wrapper.interfaces.QueryLabel;
import com.github.yulichang.wrapper.interfaces.SelectWrapper;
import com.github.yulichang.wrapper.resultmap.Label;
import com.github.yulichang.wrapper.segments.*;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 参考 {@link com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper}
 * Lambda 语法使用 Wrapper
 *
 * @author yulichang
 */
@SuppressWarnings({"unused"})
public class MPJLambdaWrapper<T> extends MPJAbstractLambdaWrapper<T, MPJLambdaWrapper<T>> implements
        Query<MPJLambdaWrapper<T>>, QueryLabel<MPJLambdaWrapper<T>>, Chain<T>, SelectWrapper<T, MPJLambdaWrapper<T>> {

    /**
     * 查询字段 sql
     */
    private SharedString sqlSelect = new SharedString();
    /**
     * 是否 select distinct
     */
    private boolean selectDistinct = false;
    /**
     * 查询的字段
     */
    @Getter
    private final List<Select> selectColumns = new ArrayList<>();
    /**
     * 映射关系
     */
    @Getter
    private final List<Label<?>> resultMapMybatisLabel = new ArrayList<>();

    /**
     * union sql
     */
    private SharedString unionSql;

    /**
     * 自定义wrapper索引
     */
    private AtomicInteger wrapperIndex;

    /**
     * 自定义wrapper
     */
    @Getter
    private Map<String, Wrapper<?>> wrapperMap;

    /**
     * 子查询wrapper
     */
    @Getter
    private MPJLambdaWrapper<T> subWrapper;

    /**
     * 推荐使用 带 class 的构造方法
     */
    public MPJLambdaWrapper() {
        super();
    }

    /**
     * 推荐使用此构造方法
     */
    public MPJLambdaWrapper(Class<T> clazz) {
        super(clazz);
    }

    /**
     * 构造方法
     *
     * @param entity 主表实体
     */
    public MPJLambdaWrapper(T entity) {
        super(entity);
    }

    /**
     * 自定义主表别名
     */
    public MPJLambdaWrapper(String alias) {
        super(alias);
    }

    /**
     * 构造方法
     *
     * @param clazz 主表class类
     * @param alias 主表别名
     */
    public MPJLambdaWrapper(Class<T> clazz, String alias) {
        super(clazz, alias);
    }

    /**
     * 构造方法
     *
     * @param entity 主表实体类
     * @param alias  主表别名
     */
    public MPJLambdaWrapper(T entity, String alias) {
        super(entity, alias);
    }

    /**
     * 不建议直接 new 该实例，使用 JoinWrappers.lambda(UserDO.class)
     */
    MPJLambdaWrapper(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
                     Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                     SharedString lastSql, SharedString sqlComment, SharedString sqlFirst,
                     TableList tableList, Integer index, String keyWord, Class<?> joinClass, String tableName) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
        this.tableList = tableList;
        this.index = index;
        this.keyWord = keyWord;
        this.joinClass = joinClass;
        this.tableName = tableName;
    }


    /**
     * sql去重
     * select distinct
     */
    public MPJLambdaWrapper<T> distinct() {
        this.selectDistinct = true;
        return typedThis;
    }


    @Override
    public List<Select> getSelectColum() {
        return this.selectColumns;
    }

    @Override
    public void addLabel(Label<?> label) {
        this.resultMap = true;
        this.resultMapMybatisLabel.add(label);
    }

    @Override
    public MPJLambdaWrapper<T> getChildren() {
        return typedThis;
    }


    /**
     * 设置查询字段
     *
     * @param columns 字段数组
     * @return children
     */
    @SafeVarargs
    public final <E> MPJLambdaWrapper<T> select(SFunction<E, ?>... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            Class<?> aClass = LambdaUtils.getEntityClass(columns[0]);
            Map<String, SelectCache> cacheMap = ColumnCache.getMapField(aClass);
            Map<String, FieldCache> fieldMap = MPJReflectionKit.getFieldMap(aClass);
            for (SFunction<E, ?> s : columns) {
                String property = LambdaUtils.getName(s);
                if (CollectionUtils.isEmpty(cacheMap)) {
                    FieldCache cache = fieldMap.get(property);
                    getSelectColum().add(new SelectProperty(cache, hasAlias, alias));
                } else {
                    SelectCache cache = cacheMap.get(property);
                    getSelectColum().add(new SelectNormal(cache, index, hasAlias, alias));
                }
            }
        }
        return typedThis;
    }

    @Override
    public MPJLambdaWrapper<T> selectAll(Class<?> clazz) {
        return Query.super.selectAll(clazz);
    }

    /**
     * 子查询
     */
    public <E, F> MPJLambdaWrapper<T> selectSub(Class<E> clazz, Consumer<MPJLambdaWrapper<E>> consumer, SFunction<F, ?> alias) {
        return selectSub(clazz, ConfigProperties.subQueryAlias, consumer, alias);
    }

    /**
     * 子查询
     */
    @SuppressWarnings("DuplicatedCode")
    public <E, F> MPJLambdaWrapper<T> selectSub(Class<E> clazz, String st, Consumer<MPJLambdaWrapper<E>> consumer, SFunction<F, ?> alias) {
        MPJLambdaWrapper<E> wrapper = new MPJLambdaWrapper<E>(null, clazz, SharedString.emptyString(), paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(),
                new TableList(), null, null, null, null) {
        };
        wrapper.tableList.setAlias(st);
        wrapper.tableList.setRootClass(clazz);
        wrapper.tableList.setParent(this.tableList);
        wrapper.alias = st;
        wrapper.subTableAlias = st;
        consumer.accept(wrapper);
        addCustomWrapper(wrapper);
        String sql = WrapperUtils.buildSubSqlByWrapper(clazz, wrapper, this.getCacheColumn(alias));
        this.selectColumns.add(new SelectString(sql, hasAlias, this.alias));
        return typedThis;
    }

    /**
     * from子查询
     */
    @SuppressWarnings("DuplicatedCode")
    public <E> MPJLambdaWrapper<T> fromSub(Consumer<MPJLambdaWrapper<T>> consumer, Class<E> tag) {
        Class<T> clazz = this.getEntityClass();
        MPJLambdaWrapper<T> wrapper = new MPJLambdaWrapper<T>(null, clazz, SharedString.emptyString(), paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(),
                new TableList(), null, null, null, this.getTableName()) {
        };
        this.hasAlias = true;
        this.tableList.setRootClass(tag);
        wrapper.tableList.setAlias(this.alias);
        wrapper.tableList.setRootClass(clazz);
        wrapper.alias = this.alias;
        wrapper.subTableAlias = this.alias;
        consumer.accept(wrapper);
        this.subWrapper = wrapper;
        String sql = WrapperUtils.buildSubSqlByWrapper(clazz, wrapper, null);
        this.setTableName(tableName -> sql);
        return typedThis;
    }

    /**
     * union
     */
    @SuppressWarnings("UnusedReturnValue")
    public final MPJLambdaWrapper<T> union(MPJLambdaWrapper<?>... wrappers) {
        StringBuilder sb = new StringBuilder();
        for (MPJLambdaWrapper<?> wrapper : wrappers) {
            addCustomWrapper(wrapper);
            Class<?> entityClass = wrapper.getEntityClass();
            Assert.notNull(entityClass, "请使用 new MPJLambdaWrapper(主表.class) 或 JoinWrappers.lambda(主表.class) 构造方法");
            sb.append(" UNION ")
                    .append(WrapperUtils.buildUnionSqlByWrapper(entityClass, wrapper));
        }
        if (Objects.isNull(unionSql)) {
            unionSql = SharedString.emptyString();
        }
        unionSql.setStringValue(unionSql.getStringValue() + sb);
        return typedThis;
    }

    /**
     * union all
     */
    @SafeVarargs
    public final <E, F> MPJLambdaWrapper<T> unionAll(MPJLambdaWrapper<T>... wrappers) {
        StringBuilder sb = new StringBuilder();
        for (MPJLambdaWrapper<?> wrapper : wrappers) {
            addCustomWrapper(wrapper);
            Class<?> entityClass = wrapper.getEntityClass();
            Assert.notNull(entityClass, "请使用 new MPJLambdaWrapper(主表.class) 或 JoinWrappers.lambda(主表.class) 构造方法");
            sb.append(" UNION ALL ")
                    .append(WrapperUtils.buildUnionSqlByWrapper(entityClass, wrapper));
        }
        if (Objects.isNull(unionSql)) {
            unionSql = SharedString.emptyString();
        }
        unionSql.setStringValue(unionSql.getStringValue() + sb);
        return typedThis;
    }

    @SuppressWarnings("DuplicatedCode")
    private void addCustomWrapper(MPJLambdaWrapper<?> wrapper) {
        if (Objects.isNull(wrapperIndex)) {
            wrapperIndex = new AtomicInteger(0);
        }
        int index = wrapperIndex.incrementAndGet();
        if (Objects.isNull(wrapperMap)) {
            wrapperMap = new HashMap<>();
        }
        String key = "ew" + index;
        wrapper.setParamAlias(wrapper.getParamAlias() + ".wrapperMap." + key);
        wrapperMap.put(key, wrapper);
    }

    /**
     * 查询条件 SQL 片段
     */
    @Override
    @SuppressWarnings("DuplicatedCode")
    public String getSqlSelect() {
        if (StringUtils.isBlank(sqlSelect.getStringValue()) && CollectionUtils.isNotEmpty(selectColumns)) {
            String s = selectColumns.stream().map(i -> {
                if (i.isStr()) {
                    return i.getColumn();
                }
                String prefix;
                if (i.isHasTableAlias()) {
                    prefix = i.getTableAlias();
                } else {
                    if (i.isLabel()) {
                        if (i.isHasTableAlias()) {
                            prefix = i.getTableAlias();
                        } else {
                            prefix = tableList.getPrefix(i.getIndex(), i.getClazz(), true);
                        }
                    } else {
                        prefix = tableList.getPrefix(i.getIndex(), i.getClazz(), false);
                    }
                }
                if (!Objects.isNull(this.subWrapper) && this.subWrapper.alias.equals(prefix)) {
                    List<Select> subSelectColumns = this.subWrapper.getSelectColum();
                    List<String> subColumns = subSelectColumns.stream().map(x ->
                            StringUtils.isNotBlank(x.getAlias()) ? x.getAlias() : x.getColumn())
                            .collect(Collectors.toList());
                    boolean noneMatch = subColumns.stream().noneMatch(x -> x.equals(i.getColumn()) ||
                            x.endsWith(Constant.AS + i.getColumn()));
                    if (noneMatch) {
                        return null;
                    }
                }
                String str = prefix + StringPool.DOT + i.getColumn();
                if (i.isFunc()) {
                    SelectFunc.Arg[] args = i.getArgs();
                    if (Objects.isNull(args) || args.length == 0) {
                        return String.format(i.getFunc().getSql(), str) + Constant.AS + i.getAlias();
                    } else {
                        return String.format(i.getFunc().getSql(), Arrays.stream(args).map(arg -> {
                            String prefixByClass = tableList.getPrefixByClass(arg.getClazz());
                            Map<String, SelectCache> mapField = ColumnCache.getMapField(arg.getClazz());
                            SelectCache cache = mapField.get(arg.getProp());
                            return prefixByClass + StringPool.DOT + cache.getColumn();
                        }).toArray()) + Constant.AS + i.getAlias();
                    }
                } else {
                    return i.isHasAlias() ? (str + Constant.AS + i.getAlias()) : str;
                }
            }).filter(x -> StringUtils.isNotBlank(x)).collect(Collectors.joining(StringPool.COMMA));
            sqlSelect.setStringValue(s);
        }
        return sqlSelect.getStringValue();
    }

    @Override
    public String getUnionSql() {
        return Optional.ofNullable(unionSql).map(SharedString::getStringValue).orElse(StringPool.EMPTY);
    }

    public boolean getSelectDistinct() {
        return selectDistinct;
    }

    /**
     * 用于生成嵌套 sql
     * <p>故 sqlSelect 不向下传递</p>
     */
    @Override
    protected MPJLambdaWrapper<T> instance() {
        return instance(index, null, null, null);
    }

    @Override
    protected MPJLambdaWrapper<T> instanceEmpty() {
        return new MPJLambdaWrapper<>();
    }

    @Override
    protected MPJLambdaWrapper<T> instance(Integer index, String keyWord, Class<?> joinClass, String tableName) {
        return new MPJLambdaWrapper<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(),
                this.tableList, index, keyWord, joinClass, tableName);
    }

    @Override
    public void clear() {
        super.clear();
        selectDistinct = false;
        sqlSelect.toNull();
        selectColumns.clear();
        wrapperIndex = new AtomicInteger(0);
        wrapperMap.clear();
        subWrapper = null;
        resultMapMybatisLabel.clear();
    }
}
