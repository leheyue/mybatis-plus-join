package com.github.leheyue.test.kt

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@MapperScan(
    value = ["com.github.leheyue.test.kt.mapper"],
    basePackages = ["com.github.leheyue.test.kt.mapper"]
)
@ComponentScan(basePackages = ["com.github.leheyue.test"])
@SpringBootApplication
open class KtApplication

fun main(args: Array<String>) {
    runApplication<KtApplication>(*args)
}
