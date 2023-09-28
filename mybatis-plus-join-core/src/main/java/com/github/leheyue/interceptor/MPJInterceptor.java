package com.github.leheyue.interceptor;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.github.leheyue.config.ConfigProperties;
import com.github.leheyue.method.MPJResultType;
import com.github.leheyue.query.MPJQueryWrapper;
import com.github.leheyue.toolkit.Constant;
import com.github.leheyue.toolkit.MPJReflectionKit;
import com.github.leheyue.toolkit.MPJTableMapperHelper;
import com.github.leheyue.toolkit.TableHelper;
import com.github.leheyue.toolkit.support.FieldCache;
import com.github.leheyue.wrapper.interfaces.SelectWrapper;
import com.github.leheyue.wrapper.resultmap.IResult;
import com.github.leheyue.wrapper.resultmap.Label;
import com.github.leheyue.wrapper.segments.Select;
import com.github.leheyue.wrapper.segments.SelectLabel;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连表拦截器
 * 用于实现动态resultType
 * 之前的实现方式是mybatis-plus的Interceptor,无法修改args,存在并发问题
 * 所以将这个拦截器独立出来
 *
 * @author yulichang
 */
@SuppressWarnings("unchecked")
@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class MPJInterceptor implements Interceptor {


    private static final List<ResultMapping> EMPTY_RESULT_MAPPING = new ArrayList<>(0);

    /**
     * 缓存MappedStatement,不需要每次都去重新构建MappedStatement
     */
    private static final Map<String, Map<Configuration, MappedStatement>> MS_CACHE = new ConcurrentHashMap<>();


    @Override
    @SuppressWarnings("Java8MapApi")
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (args[0] instanceof MappedStatement) {
            MappedStatement ms = (MappedStatement) args[0];
            if (args[1] instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) args[1];
                Object ew = map.containsKey(Constants.WRAPPER) ? map.get(Constants.WRAPPER) : null;
                if (CollectionUtils.isNotEmpty(map) && map.containsKey(Constant.CLAZZ)) {
                    Class<?> clazz = (Class<?>) map.get(Constant.CLAZZ);
                    if (Objects.nonNull(clazz)) {
                        List<ResultMap> list = ms.getResultMaps();
                        if (CollectionUtils.isNotEmpty(list)) {
                            ResultMap resultMap = list.get(0);
                            if (resultMap.getType() == MPJResultType.class) {
                                args[0] = getMappedStatement(ms, clazz, ew);
                            }
                        }
                    }
                }
            }
        }
        return invocation.proceed();
    }


    /**
     * 获取MappedStatement
     */
    @SuppressWarnings("rawtypes")
    public MappedStatement getMappedStatement(MappedStatement ms, Class<?> resultType, Object ew) {
        String id = ms.getId() + StringPool.DASH + (resultType.getName().replaceAll("\\.", StringPool.DASH));
        if (ew instanceof SelectWrapper) {
            SelectWrapper wrapper = (SelectWrapper) ew;
            if (wrapper.getEntityClass() == null) {
                wrapper.setEntityClass(MPJTableMapperHelper.getEntity(getEntity(ms.getId(), ms.getResource())));
            }
            if (wrapper.getSelectColumns().isEmpty() && wrapper.getEntityClass() != null) {
                wrapper.selectAll(wrapper.getEntityClass());
            }
            //TODO 重构缓存 -> 根据sql缓存
            //不走缓存
        }
        if (ew instanceof MPJQueryWrapper) {
            MPJQueryWrapper wrapper = (MPJQueryWrapper) ew;
            if (ConfigProperties.msCache) {
                return getCache(ms, id + StringPool.UNDERSCORE + removeDot(wrapper.getSqlSelect()), resultType, ew);
            }
        }
        return buildMappedStatement(ms, resultType, ew, id);
    }


    /**
     * 走缓存
     */
    private MappedStatement getCache(MappedStatement ms, String id, Class<?> resultType, Object ew) {
        Map<Configuration, MappedStatement> statementMap = MS_CACHE.get(id);
        if (CollectionUtils.isNotEmpty(statementMap)) {
            MappedStatement statement = statementMap.get(ms.getConfiguration());
            if (Objects.nonNull(statement)) {
                return statement;
            }
        }
        MappedStatement mappedStatement = buildMappedStatement(ms, resultType, ew, id);
        if (statementMap == null) {
            statementMap = new ConcurrentHashMap<>();
            MS_CACHE.put(id, statementMap);
        }
        statementMap.put(ms.getConfiguration(), mappedStatement);
        return mappedStatement;
    }


    /**
     * 构建新的MappedStatement
     */
    private MappedStatement buildMappedStatement(MappedStatement ms, Class<?> resultType, Object ew, String id) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), id, ms.getSqlSource(), ms.getSqlCommandType())
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .statementType(ms.getStatementType())
                .keyGenerator(ms.getKeyGenerator())
                .timeout(ms.getTimeout())
                .parameterMap(ms.getParameterMap())
                .resultSetType(ms.getResultSetType())
                .cache(ms.getCache())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            builder.keyProperty(String.join(StringPool.COMMA, ms.getKeyProperties()));
        }
        List<ResultMap> resultMaps = buildResultMap(ms, resultType, ew);
        builder.resultMaps(resultMaps);
        return builder.build();
    }

    /**
     * 构建resultMap TODO 可以用lambda简化代码
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<ResultMap> buildResultMap(MappedStatement ms, Class<?> resultType, Object obj) {
        List<ResultMap> result = new ArrayList<>();
        TableInfo tableInfo = TableHelper.get(resultType);
        String id = ms.getId() + StringPool.DOT + Constants.MYBATIS_PLUS + StringPool.UNDERSCORE + resultType.getName();
        //基本数据类型
        if (MPJReflectionKit.isPrimitiveOrWrapper(resultType)) {
            return Collections.singletonList(new ResultMap.Builder(ms.getConfiguration(), id, resultType, EMPTY_RESULT_MAPPING).build());
        }
        if (!(obj instanceof SelectWrapper) || Map.class.isAssignableFrom(resultType) ||
                Collection.class.isAssignableFrom(resultType)) {
            result.add(getDefaultResultMap(tableInfo, ms, resultType, id));
            return result;
        }
        SelectWrapper wrapper = (SelectWrapper) obj;
        Map<String, FieldCache> fieldMap = MPJReflectionKit.getFieldMap(resultType);
        List<Select> columnList = wrapper.getSelectColumns();
        //移除对多查询列，为了可重复使用wrapper
        columnList.removeIf(Select::isLabel);
        List<ResultMapping> resultMappings = new ArrayList<>();
        Set<String> columnSet = new HashSet<>();
        for (Select i : columnList) {
            if (i.isHasAlias()) {
                FieldCache field = fieldMap.get(i.getAlias());
                columnSet.add(i.getAlias());
                if (Objects.nonNull(field)) {
                    ResultMapping.Builder builder = new ResultMapping.Builder(ms.getConfiguration(), i.getAlias(),
                            i.getAlias(), field.getType());
                    resultMappings.add(selectToResult(wrapper.getEntityClass(), i, field.getType(), builder));
                }
            } else {
                FieldCache field = fieldMap.get(i.getColumProperty());
                columnSet.add(i.getTagColumn());
                if (Objects.nonNull(field)) {
                    ResultMapping.Builder builder = new ResultMapping.Builder(ms.getConfiguration(), i.getColumProperty(),
                            i.getTagColumn(), field.getType());
                    resultMappings.add(selectToResult(wrapper.getEntityClass(), i, field.getType(), builder));
                }
            }
        }
        if (wrapper.isResultMap()) {
            for (Object o : wrapper.getResultMapMybatisLabel()) {
                Label<?> label = (Label<?>) o;
                resultMappings.add(buildResult(ms, label, columnSet, columnList));
            }
        }
        result.add(new ResultMap.Builder(ms.getConfiguration(), id, resultType, resultMappings).build());
        return result;
    }

    private ResultMapping selectToResult(Class<?> entity, Select select, Class<?> type, ResultMapping.Builder builder) {
        if (select.hasTypeHandle() && select.getTableFieldInfo().getPropertyType().isAssignableFrom(type)) {
            builder.typeHandler(select.getTypeHandle());
        }
        if (select.isPk() && entity == select.getClazz()) {
            builder.flags(Collections.singletonList(ResultFlag.ID));
        }
        return builder.build();
    }


    //fix 重上往下会有resultMap覆盖问题,应该从根节点开始,id向上传递

    /**
     * @return 返回节点id
     */
    private ResultMapping buildResult(MappedStatement ms, Label<?> mybatisLabel, Set<String> columnSet,
                                      List<Select> columnList) {
        List<IResult> resultList = mybatisLabel.getResultList();
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }
        StringBuilder childId = new StringBuilder("MPJ_")
                .append(mybatisLabel.getOfType().getName())
                .append(StringPool.UNDERSCORE)
                .append(mybatisLabel.getProperty())
                .append(StringPool.UNDERSCORE);
        List<ResultMapping> childMapping = new ArrayList<>(resultList.size());
        for (IResult r : resultList) {
            childId.append("(");
            Map<String, FieldCache> ofTypeField = MPJReflectionKit.getFieldMap(mybatisLabel.getOfType());
            //列名去重
            String columnName = StringUtils.getTargetColumn(r.getSelectNormal().getColumn());
            SelectLabel label;
            FieldCache field = ofTypeField.get(r.getProperty());
            String index = r.getIndex();
            if (columnSet.contains(columnName)) {
                columnName = getColumn(columnSet, columnName, 0);
                label = new SelectLabel(r.getSelectNormal(), null, mybatisLabel.getOfType(), columnName, StringUtils.isNotBlank(index), index);
            } else {
                columnSet.add(columnName);
                label = new SelectLabel(r.getSelectNormal(), null, mybatisLabel.getOfType(), StringUtils.isNotBlank(index), index);
            }
            columnList.add(label);
            ResultMapping.Builder builder = new ResultMapping.Builder(ms.getConfiguration(), r.getProperty(), columnName, r.getJavaType());
            if (r.isId()) {//主键标记为id标签
                builder.flags(Collections.singletonList(ResultFlag.ID));
                childId.append("i");
            } else {
                childId.append("c");
            }
            //TypeHandle
            if (label.hasTypeHandle() && label.getColumnType().isAssignableFrom(field.getType())) {
                builder.typeHandler(label.getTypeHandle());
            }
            if (Objects.nonNull(r.getJdbcType())) {
                builder.jdbcType(r.getJdbcType());
            }
            childMapping.add(builder.build());
            childId.append(StringPool.DASH)
                    .append(columnName)
                    .append(StringPool.DASH)
                    .append(r.getProperty())
                    .append(")");
        }

        if (CollectionUtils.isNotEmpty(mybatisLabel.getMybatisLabels())) {
            //递归调用
            childId.append("[");
            for (Object it : mybatisLabel.getMybatisLabels()) {
                Label<?> o = (Label<?>) it;
                if (Objects.isNull(o)) {
                    continue;
                }
                ResultMapping result = buildResult(ms, o, columnSet, columnList);
                if (Objects.isNull(result)) {
                    continue;
                }
                childMapping.add(result);
                childId.append(result.getNestedResultMapId());
                childId.append("@");
            }
            childId.append("]");
        }
        //双检
        String id = childId.toString();
        if (!ms.getConfiguration().hasResultMap(id)) {
            ResultMap build = new ResultMap.Builder(ms.getConfiguration(), id, mybatisLabel.getOfType(), childMapping).build();
            MPJInterceptor.addResultMap(ms, id, build);
        }
        return new ResultMapping.Builder(ms.getConfiguration(), mybatisLabel.getProperty())
                .javaType(mybatisLabel.getJavaType())
                .nestedResultMapId(id)
                .build();
    }

    /**
     * 列名去重 重复的添加 mpj 前缀 再重复走递归
     *
     * @param pool       查询列 集合
     * @param columnName 列明
     * @return 唯一列名
     */
    private String getColumn(Set<String> pool, String columnName, int num) {
        String newName = ConfigProperties.joinPrefix + getPrefix(num) + StringPool.UNDERSCORE + columnName;
        if (!pool.contains(newName)) {
            pool.add(newName);
            return newName;
        }
        return getColumn(pool, columnName, ++num);
    }

    private String getPrefix(int num) {
        String s = Integer.toString(num, 25);
        char[] array = s.toCharArray();
        char[] chars = new char[s.length()];
        for (int i = 0; i < array.length; i++) {
            chars[i] = (char) (array[i] < 58 ? (array[i] + 49) : (array[i] + 10));
        }
        return new String(chars);
    }

    /**
     * 获取非lambda的resultMap
     */
    private ResultMap getDefaultResultMap(TableInfo tableInfo, MappedStatement ms, Class<?> resultType, String id) {
        if (tableInfo != null && tableInfo.isAutoInitResultMap()) {
            //补充不全的属性 并且是基础数据类型及其包装类
            ResultMap resultMap = ms.getConfiguration().getResultMap(tableInfo.getResultMap());
            List<ResultMapping> resultMappings = resultMap.getResultMappings();
            List<Field> fieldList = ReflectionKit.getFieldList(resultType);
            fieldList.removeIf(i -> resultMappings.stream().anyMatch(r -> i.getName().equals(r.getProperty())));
            if (CollectionUtils.isNotEmpty(fieldList)) {
                //复制已有的resultMapping
                List<ResultMapping> resultMappingList = new ArrayList<>(resultMappings);
                //复制不存在的resultMapping
                for (Field i : fieldList) {
                    if (MPJReflectionKit.isPrimitiveOrWrapper(i.getType())) {
                        resultMappingList.add(new ResultMapping.Builder(ms.getConfiguration(),
                                i.getName(), i.getName(), i.getType()).build());
                    }
                }
                return new ResultMap.Builder(ms.getConfiguration(), id, resultType, resultMappingList).build();
            }
        }
        return new ResultMap.Builder(ms.getConfiguration(), id, resultType, EMPTY_RESULT_MAPPING).build();
    }


    /**
     * 往 Configuration 添加resultMap
     */
    private synchronized static void addResultMap(MappedStatement ms, String key, ResultMap resultMap) {
        if (!ms.getConfiguration().hasResultMap(key)) {
            ms.getConfiguration().addResultMap(resultMap);
        }
    }

    private Class<?> getEntity(String id, String resource) {
        Class<?> clazz = null;
        try {
            String className = id.substring(0, id.lastIndexOf(StringPool.DOT));
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                clazz = MPJTableMapperHelper.getMapperForName(className);
            }
        } catch (Exception ignored) {
        }
        if (Objects.nonNull(clazz)) {
            return clazz;
        }
        try {
            String className = resource.substring(0, id.lastIndexOf(StringPool.DOT)).replaceAll("/", StringPool.DOT);
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                clazz = MPJTableMapperHelper.getMapperForName(className);
            }
        } catch (Exception ignored) {
        }
        return clazz;
    }

    private String removeDot(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        } else {
            return str.replaceAll("\\.", StringPool.DASH);
        }
    }

    @Override
    public Object plugin(Object target) {
        try {
            return Interceptor.super.plugin(target);
        } catch (Exception e) {
            return Plugin.wrap(target, this);
        }
    }

    @Override
    public void setProperties(Properties properties) {
        try {
            Interceptor.super.setProperties(properties);
        } catch (Exception ignored) {
        }
    }
}
