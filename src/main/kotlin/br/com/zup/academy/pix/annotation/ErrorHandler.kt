package br.com.zup.academy.pix.annotation

import br.com.zup.academy.pix.exception.ExceptionHandlerInterceptor
import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type
import kotlin.annotation.AnnotationTarget.CLASS

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(CLASS)
@Around
@Type(ExceptionHandlerInterceptor::class)
annotation class ErrorHandler()
