package com.eric.projects.example;

import com.eric.projects.aop.advice.AroundAdvice;
import com.eric.projects.aop.annotation.Aspect;
import com.eric.projects.core.annotation.Controller;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@Aspect(target = Controller.class)
public class LightningAspect implements AroundAdvice {
    @Override
    public void before(Class<?> clazz, Method method, Object[] args) throws Throwable {
        log.info("Before  LightningAspect ----> class: {}, method: {}", clazz.getName(), method.getName());
    }

    @Override
    public void afterReturning(Class<?> clazz, Method method, Object[] args) throws Throwable {
        log.info("After  LightningAspect ----> class: {}, method: {}", clazz, method.getName());
    }


    @Override
    public void afterThrowing(Class<?> clazz, Method method, Object[] args, Throwable e) {
        log.error("Error  LightningAspect ----> class: {}, method: {}, exception: {}", clazz, method.getName(), e.getMessage());
    }
}
