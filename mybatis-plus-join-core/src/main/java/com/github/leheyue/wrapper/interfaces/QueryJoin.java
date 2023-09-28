package com.github.leheyue.wrapper.interfaces;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.leheyue.interfaces.MPJBaseJoin;
import com.github.leheyue.query.interfaces.StringJoin;
import com.github.leheyue.toolkit.Constant;
import com.github.leheyue.wrapper.MPJAbstractLambdaWrapper;

import java.util.function.BiConsumer;

/**
 * @author yulichang
 */
@SuppressWarnings("unused")
public interface QueryJoin<Children, Entity> extends MPJBaseJoin<Entity>, StringJoin<Children, Entity> {

    /**
     * left join
     *
     * @param clazz 关联的实体类
     * @param left  条件
     * @param right 条件
     */
    default <T, X> Children leftJoin(Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(Constant.LEFT_JOIN, clazz, left, right);
    }

    /**
     * left join 多条件
     * <p>
     * 例 leftJoin(UserDO.class, on -> on.eq(UserDO::getId,UserAddressDO::getUserId).le().gt()...)
     *
     * @param clazz    关联实体类
     * @param function 条件`
     */
    default <T> Children leftJoin(Class<T> clazz, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(Constant.LEFT_JOIN, clazz, function);
    }

    /**
     * left join
     *
     * @param clazz 关联的实体类
     * @param left  条件
     * @param right 条件
     */
    default <T, X> Children leftJoin(Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(Constant.LEFT_JOIN, clazz, left, right, ext);
    }

    /**
     * left join 多条件
     * <p>
     * 例 leftJoin(UserDO.class, on -> on.eq(UserDO::getId,UserAddressDO::getUserId).le().gt()...)
     *
     * @param clazz    关联实体类
     * @param consumer 条件
     */
    default <T> Children leftJoin(Class<T> clazz, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(Constant.LEFT_JOIN, clazz, consumer);
    }

    /**
     * left join
     *
     * @param clazz 关联的实体类
     * @param left  条件
     * @param right 条件
     */
    default <T, X> Children leftJoin(Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(Constant.LEFT_JOIN, clazz, alias, left, right);
    }

    /**
     * left join 多条件
     * <p>
     * 例 leftJoin(UserDO.class, on -> on.eq(UserDO::getId,UserAddressDO::getUserId).le().gt()...)
     *
     * @param clazz    关联实体类
     * @param function 条件
     */
    default <T> Children leftJoin(Class<T> clazz, String alias, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(Constant.LEFT_JOIN, clazz, alias, function);
    }

    /**
     * left join
     *
     * @param clazz 关联的实体类
     * @param left  条件
     * @param right 条件
     */
    default <T, X> Children leftJoin(Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(Constant.LEFT_JOIN, clazz, alias, left, right, ext);
    }

    /**
     * left join 多条件
     * <p>
     * 例 leftJoin(UserDO.class, on -> on.eq(UserDO::getId,UserAddressDO::getUserId).le().gt()...)
     *
     * @param clazz    关联实体类
     * @param consumer 条件
     */
    default <T> Children leftJoin(Class<T> clazz, String alias, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(Constant.LEFT_JOIN, clazz, alias, consumer);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children rightJoin(Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(Constant.RIGHT_JOIN, clazz, left, right);
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children rightJoin(Class<T> clazz, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(Constant.RIGHT_JOIN, clazz, function);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children rightJoin(Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(Constant.RIGHT_JOIN, clazz, left, right, ext);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children rightJoin(Class<T> clazz, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(Constant.RIGHT_JOIN, clazz, consumer);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children rightJoin(Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(Constant.RIGHT_JOIN, clazz, alias, left, right);
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children rightJoin(Class<T> clazz, String alias, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(Constant.RIGHT_JOIN, clazz, alias, function);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children rightJoin(Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(Constant.RIGHT_JOIN, clazz, alias, left, right, ext);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children rightJoin(Class<T> clazz, String alias, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(Constant.RIGHT_JOIN, clazz, alias, consumer);
    }


    /**
     * ignore 参考 left join
     */
    default <T, X> Children innerJoin(Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(Constant.INNER_JOIN, clazz, on -> on.eq(left, right));
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children innerJoin(Class<T> clazz, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(Constant.INNER_JOIN, clazz, function);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children innerJoin(Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(Constant.INNER_JOIN, clazz, left, right, ext);
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children innerJoin(Class<T> clazz, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(Constant.INNER_JOIN, clazz, consumer);
    }


    /**
     * ignore 参考 left join
     */
    default <T, X> Children innerJoin(Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(Constant.INNER_JOIN, clazz, alias, on -> on.eq(left, right));
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children innerJoin(Class<T> clazz, String alias, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(Constant.INNER_JOIN, clazz, alias, function);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children innerJoin(Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(Constant.INNER_JOIN, clazz, alias, left, right, ext);
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children innerJoin(Class<T> clazz, String alias, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(Constant.INNER_JOIN, clazz, alias, consumer);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children fullJoin(Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(Constant.FULL_JOIN, clazz, left, right);
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children fullJoin(Class<T> clazz, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(Constant.FULL_JOIN, clazz, function);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children fullJoin(Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(Constant.FULL_JOIN, clazz, left, right, ext);
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children fullJoin(Class<T> clazz, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(Constant.FULL_JOIN, clazz, consumer);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children fullJoin(Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(Constant.FULL_JOIN, clazz, alias, left, right);
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children fullJoin(Class<T> clazz, String alias, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(Constant.FULL_JOIN, clazz, alias, function);
    }

    /**
     * ignore 参考 left join
     */
    default <T, X> Children fullJoin(Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(Constant.FULL_JOIN, clazz, alias, left, right, ext);
    }

    /**
     * ignore 参考 left join
     */
    default <T> Children fullJoin(Class<T> clazz, String alias, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(Constant.FULL_JOIN, clazz, alias, consumer);
    }

    /**
     * 自定义连表关键词
     * 调用此方法 keyword 前后需要带空格 比如 " LEFT JOIN "  " RIGHT JOIN "
     * <p>
     * 查询基类 可以直接调用此方法实现以上所有功能
     *
     * @param keyWord 连表关键字
     * @param clazz   连表实体类
     * @param left    关联条件
     * @param right   扩展 用于关联表的 select 和 where
     */
    default <T, X> Children join(String keyWord, Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(keyWord, clazz, on -> on.eq(left, right));
    }

    /**
     * 自定义连表关键词
     * <p>
     * 例 leftJoin(UserDO.class, on -> on.eq(UserDO::getId,UserAddressDO::getUserId).le().gt()...)
     *
     * @param clazz    关联实体类
     * @param function 条件
     */
    default <T> Children join(String keyWord, Class<T> clazz, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(keyWord, clazz, (on, e) -> function.apply(on));
    }

    /**
     * 自定义连表关键词
     *
     * @param clazz 关联的实体类
     * @param left  条件
     * @param right 条件
     */
    default <T, X> Children join(String keyWord, Class<T> clazz, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(keyWord, clazz, (on, e) -> {
            on.eq(left, right);
            ext.apply(e);
        });
    }

    /**
     * 自定义连表关键词
     * 调用此方法 keyword 前后需要带空格 比如 " LEFT JOIN "  " RIGHT JOIN "
     * <p>
     * 查询基类 可以直接调用此方法实现以上所有功能
     *
     * @param keyWord 连表关键字
     * @param clazz   连表实体类
     * @param left    关联条件
     * @param right   扩展 用于关联表的 select 和 where
     */
    default <T, X> Children join(String keyWord, Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right) {
        return join(keyWord, clazz, alias, on -> on.eq(left, right));
    }

    /**
     * 自定义连表关键词
     * <p>
     * 例 leftJoin(UserDO.class, on -> on.eq(UserDO::getId,UserAddressDO::getUserId).le().gt()...)
     *
     * @param clazz    关联实体类
     * @param function 条件
     */
    default <T> Children join(String keyWord, Class<T> clazz, String alias, WrapperFunction<MPJAbstractLambdaWrapper<Entity, ?>> function) {
        return join(keyWord, clazz, alias, (on, e) -> function.apply(on));
    }

    /**
     * 自定义连表关键词
     *
     * @param clazz 关联的实体类
     * @param left  条件
     * @param right 条件
     */
    default <T, X> Children join(String keyWord, Class<T> clazz, String alias, SFunction<T, ?> left, SFunction<X, ?> right, WrapperFunction<Children> ext) {
        return join(keyWord, clazz, alias, (on, e) -> {
            on.eq(left, right);
            ext.apply(e);
        });
    }

    /**
     * 内部使用, 不建议直接调用
     */
    default <T> Children join(String keyWord, Class<T> clazz, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer) {
        return join(keyWord, clazz, null, consumer);
    }

    /**
     * 内部使用, 不建议直接调用
     */
    <T> Children join(String keyWord, Class<T> clazz, String alias, BiConsumer<MPJAbstractLambdaWrapper<Entity, ?>, Children> consumer);
}
