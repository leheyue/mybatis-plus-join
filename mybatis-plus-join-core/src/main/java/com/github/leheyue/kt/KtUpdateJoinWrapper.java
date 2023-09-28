package com.github.leheyue.kt;

import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.github.leheyue.adapter.AdapterHelper;
import com.github.leheyue.kt.interfaces.Update;
import com.github.leheyue.toolkit.Asserts;
import com.github.leheyue.toolkit.KtUtils;
import com.github.leheyue.toolkit.TableHelper;
import com.github.leheyue.toolkit.TableList;
import kotlin.reflect.KProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author yulichang
 * @since 1.4.6
 */
@SuppressWarnings({"unused", "DuplicatedCode"})
public class KtUpdateJoinWrapper<T> extends KtAbstractLambdaWrapper<T, KtUpdateJoinWrapper<T>>
        implements Update<KtUpdateJoinWrapper<T>> {
    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private final SharedString sqlSetStr = new SharedString();
    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private List<String> sqlSet;
    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private List<UpdateSet> updateSet;
    /**
     * SQL 更新实体(更新非空字段)
     */
    private List<Object> updateEntity;
    /**
     * SQL 更新实体(空字段也会更新)
     */
    private List<Object> updateEntityNull;

    private KtUpdateJoinWrapper() {
        super();
    }

    /**
     * 推荐使用此构造方法
     */
    public KtUpdateJoinWrapper(Class<T> clazz) {
        super(clazz);
    }

    public KtUpdateJoinWrapper(T entity) {
        super(entity);
    }

    public KtUpdateJoinWrapper(Class<T> clazz, String alias) {
        super(clazz, alias);
    }

    public KtUpdateJoinWrapper(T entity, String alias) {
        super(entity, alias);
    }

    /**
     * 设置更新的实体set语句部分, 更新非空字段
     * <p>
     * 注意!!!
     * 这里这是的实体类是set部分, 不作为条件, where条件是wrapper.setEntity()
     */
    public KtUpdateJoinWrapper<T> setUpdateEntity(Object... entity) {
        if (Objects.isNull(updateEntity)) {
            updateEntity = new ArrayList<>();
        }
        for (Object obj : entity) {
            Assert.notNull(obj, "更新实体不能为空");
            updateEntity.add(obj);
        }
        return typedThis;
    }

    /**
     * 设置更新的实体set语句部分, 更新非空字段
     * <p>
     * 注意!!!
     * 这里这是的实体类是set部分, 不作为条件, where条件是wrapper.setEntity()
     */
    public KtUpdateJoinWrapper<T> setUpdateEntityAndNull(Object... entity) {
        if (Objects.isNull(updateEntityNull)) {
            updateEntityNull = new ArrayList<>();
        }
        for (Object obj : entity) {
            Assert.notNull(obj, "更新实体不能为空");
            updateEntityNull.add(obj);
        }
        return typedThis;
    }

    @Override
    public KtUpdateJoinWrapper<T> set(boolean condition, KProperty<?> column, Object val, String mapping) {
        return maybeDo(condition, () -> {
            if (Objects.isNull(updateSet)) {
                updateSet = new ArrayList<>();
            }
            updateSet.add(new UpdateSet(column, val, mapping));
        });
    }

    @Override
    public KtUpdateJoinWrapper<T> setSql(boolean condition, String sql) {
        if (condition && StringUtils.isNotBlank(sql)) {
            if (Objects.isNull(sqlSet)) {
                sqlSet = new ArrayList<>();
            }
            sqlSet.add(sql);
        }
        return typedThis;
    }

    @Override
    public String getSqlSet() {
        if (StringUtils.isNotBlank(sqlSetStr.getStringValue())) {
            return sqlSetStr.getStringValue();
        }
        StringBuilder set = new StringBuilder(StringPool.EMPTY);
        if (CollectionUtils.isNotEmpty(updateSet)) {
            set = new StringBuilder(updateSet.stream().map(i -> tableList.getPrefixByClass(KtUtils.ref(i.getColumn())) +
                            Constants.DOT + getCache(i.getColumn()).getColumn() + Constants.EQUALS + formatParam(i.mapping, i.value))
                    .collect(Collectors.joining(StringPool.COMMA)) + StringPool.COMMA);
        }
        if (CollectionUtils.isNotEmpty(sqlSet)) {
            set.append(String.join(StringPool.COMMA, sqlSet)).append(StringPool.COMMA);
        }
        if (CollectionUtils.isNotEmpty(updateEntity)) {
            getSqlByEntity(set, true, updateEntity);
        }
        if (CollectionUtils.isNotEmpty(updateEntityNull)) {
            getSqlByEntity(set, false, updateEntityNull);
        }
        sqlSetStr.setStringValue(set.toString());
        return set.toString();
    }

    /**
     * 用于生成嵌套 sql
     * <p>故 sqlSelect 不向下传递</p>
     */
    @Override
    protected KtUpdateJoinWrapper<T> instance() {
        return instance(index, null, null, null);
    }

    @Override
    protected KtUpdateJoinWrapper<T> instanceEmpty() {
        return new KtUpdateJoinWrapper<>();
    }

    @Override
    protected KtUpdateJoinWrapper<T> instance(Integer index, String keyWord, Class<?> joinClass, String tableName) {
        return new KtUpdateJoinWrapper<>(getEntity(), getEntityClass(), paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(),
                this.tableList, index, keyWord, joinClass, tableName);
    }

    /**
     * 不建议直接 new 该实例，使用 JoinWrappers.update(User.class)
     */
    public KtUpdateJoinWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq,
                               Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                               SharedString lastSql, SharedString sqlComment, SharedString sqlFirst,
                               TableList tableList, Integer index, String keyWord, Class<?> joinClass, String tableName) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
        this.tableList = tableList;
        this.index = index;
        this.keyWord = keyWord;
        this.joinClass = joinClass;
        this.tableName = tableName;
    }

    private void getSqlByEntity(StringBuilder sb, boolean filterNull, List<Object> entityList) {
        for (Object obj : entityList) {
            Assert.isTrue(tableList.contain(obj.getClass()), "更新的实体不是主表或关联表 <%>", obj.getClass().getSimpleName());
            TableInfo tableInfo = TableHelper.get(obj.getClass());
            Asserts.hasTable(tableInfo, obj.getClass());
            for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (AdapterHelper.getTableInfoAdapter().mpjHasLogic(tableInfo) && fieldInfo.isLogicDelete()) {
                    continue;
                }
                Object val;
                try {
                    Field field = AdapterHelper.getTableInfoAdapter().mpjGetField(fieldInfo, () -> {
                        Field field1 = ReflectionKit.getFieldMap(obj.getClass()).get(fieldInfo.getProperty());
                        field1.setAccessible(true);
                        return field1;
                    });
                    val = field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (filterNull && Objects.isNull(val)) {
                    continue;
                }
                sb.append(tableList.getPrefixByClass(obj.getClass())).append(Constants.DOT)
                        .append(fieldInfo.getColumn()).append(Constants.EQUALS).append(formatParam(null, val))
                        .append(StringPool.COMMA);
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        sqlSetStr.toNull();
        if (CollectionUtils.isNotEmpty(sqlSet)) {
            sqlSet.clear();
        }
        if (CollectionUtils.isNotEmpty(updateSet)) {
            updateSet.clear();
        }
        if (CollectionUtils.isNotEmpty(updateEntity)) {
            updateEntity.clear();
        }
        if (CollectionUtils.isNotEmpty(updateEntityNull)) {
            updateEntityNull.clear();
        }
    }


    @Data
    @AllArgsConstructor
    public static class UpdateSet {

        private KProperty<?> column;

        private Object value;

        private String mapping;
    }
}
