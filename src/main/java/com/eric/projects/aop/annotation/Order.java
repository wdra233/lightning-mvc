package com.eric.projects.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
    /**
     * Execution order of proxyAdvisor, the less the value, the sooner it will get executed
     * @return
     */
    int value() default 0;
}
