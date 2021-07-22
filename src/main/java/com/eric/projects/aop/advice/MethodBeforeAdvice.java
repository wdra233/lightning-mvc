package com.eric.projects.aop.advice;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends Advice {

    void before(Class<?> clazz, Method method, Object[] args) throws Throwable;
}

