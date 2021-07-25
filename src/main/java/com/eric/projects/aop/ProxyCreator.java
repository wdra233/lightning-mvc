package com.eric.projects.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Proxy Creator: create instance for each proxy class
 */
public class ProxyCreator {

    public static Object createProxy(Class<?> targetClass, List<ProxyAdvisor> proxyList) {
        return Enhancer.create(targetClass, new AdviceMethodInterceptor(targetClass, proxyList));
    }

    private static class AdviceMethodInterceptor implements MethodInterceptor {

        private final Class<?> targetClass;

        private List<ProxyAdvisor> proxyList;

        public AdviceMethodInterceptor(Class<?> targetClass, List<ProxyAdvisor> proxyList) {
            this.targetClass = targetClass;
            this.proxyList = proxyList;
        }

        @Override
        public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return new AdviceChain(targetClass, target, method, args, methodProxy, proxyList).doAdviceChain();
        }
    }
}
