package com.github.leheyue.annotation;

/**
 * 映射表条件
 * 用法参考 mybatis plus wrapper 的 .apply()方法
 *
 * @author yulichang
 * @since 1.2.0
 */
@SuppressWarnings("unused")
public @interface Apply {

    /**
     * sql片段
     */
    String value();

    /**
     * .apply() 对应的可变参数
     */
    String[] args() default {};
}
