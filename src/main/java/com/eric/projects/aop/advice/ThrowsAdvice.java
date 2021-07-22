package com.eric.projects.aop.advice;

import java.lang.reflect.Method;

/**
 * Exception advice interface
 */
public interface ThrowsAdvice extends Advice {

    void afterThrowing(Class<?> clazz, Method method, Object[] args, Throwable e);
}
