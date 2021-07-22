package com.eric.projects.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Proxy Creator: create instance for each proxy class
 */
public class ProxyCreator {

    public static Object createProxy(Class<?> targetClass, ProxyAdvisor proxyAdvisor) {
        return Enhancer.create(targetClass,
                (MethodInterceptor) (target, method, args, proxy) ->
                proxyAdvisor.doProxy(target, targetClass, method, args, proxy));
    }
}
