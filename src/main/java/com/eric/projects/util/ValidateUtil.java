package com.eric.projects.util;

public final class ValidateUtil {

    /**
     * Determine if obj is null or not
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    /**
     * Determine if a string is empty or null
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }




}
