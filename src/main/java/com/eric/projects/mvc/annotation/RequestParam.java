package com.eric.projects.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {

    /**
     * Alias name for method parameter
     */
    String value() default "";

    /**
     * Required filed for passing parameter
     */
    boolean required() default true;
}
