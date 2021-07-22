package com.eric.projects.aop.advice;

import java.lang.reflect.Method;

public interface AfterReturningAdvice extends Advice {

    void afterReturning(Class<?> clazz, Method method, Object[] args) throws Throwable;
}
