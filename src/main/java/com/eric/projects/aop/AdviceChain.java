package com.eric.projects.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Implementations of multiple proxies
 */
public class AdviceChain {

    @Getter
    private final Class<?> targetClass;

    @Getter
    private final Object target;

    @Getter
    private final Method method;

    @Getter
    private final Object[] args;

    private final MethodProxy methodProxy;

    /**
     * a list of proxyAdvisors that will do adviceChain
     */
    private List<ProxyAdvisor> proxyList;

    private int adviceIndex = 0;

    public AdviceChain(Class<?> targetClass, Object target, Method method, Object[] args, MethodProxy methodProxy, List<ProxyAdvisor> proxyList) {
        this.targetClass = targetClass;
        this.target = target;
        this.method = method;
        this.args = args;
        this.methodProxy = methodProxy;
        this.proxyList = proxyList;
    }

    public Object doAdviceChain() throws Throwable {
        while (adviceIndex < proxyList.size()
                && !proxyList.get(adviceIndex).getPointcut().matches(method)) {
            adviceIndex++;
        }

        Object result;
        if (adviceIndex < proxyList.size()) {
            result = proxyList.get(adviceIndex++).doProxy(this);
        } else {
            result = methodProxy.invokeSuper(target, args);
        }

        return result;
    }



}
