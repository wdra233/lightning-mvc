package com.eric.projects.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    /**
     * pointcut expression
     * @return
     */
    String pointcut() default "";
}
