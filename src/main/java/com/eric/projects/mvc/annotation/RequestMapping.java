package com.eric.projects.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
public @interface RequestMapping {

    /**
     * Request Path
     * @return
     */
    String value() default "";

    RequestMethod method() default RequestMethod.GET;
}
