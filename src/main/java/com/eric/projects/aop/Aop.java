package com.eric.projects.aop;

import com.eric.projects.aop.advice.Advice;
import com.eric.projects.aop.annotation.Aspect;
import com.eric.projects.aop.annotation.Order;
import com.eric.projects.core.BeanContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Aop {
    private BeanContainer beanContainer;

    public Aop() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doAop() {
        List<ProxyAdvisor> proxyList = beanContainer.getClassesBySuper(Advice.class)
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(Aspect.class))
                .map(this::createProxyAdvisor)
                .collect(Collectors.toList());

        beanContainer.getClasses()
                .stream()
                .filter(clazz -> !Advice.class.isAssignableFrom(clazz))
                .filter(clazz -> !clazz.isAnnotationPresent(Aspect.class))
                .forEach(clazz -> {
                    List<ProxyAdvisor> matchProxies = createMatchProxies(proxyList, clazz);
                    if (matchProxies.size() > 0) {
                        Object proxyBean = ProxyCreator.createProxy(clazz, matchProxies);
                        beanContainer.addBean(clazz, proxyBean);
                    }
                });
    }

    private ProxyAdvisor createProxyAdvisor(Class<?> aspectClass) {
        int order = 0;
        if (aspectClass.isAnnotationPresent(Order.class)) {
            order = aspectClass.getAnnotation(Order.class).value();
        }

        final String expression  = aspectClass.getAnnotation(Aspect.class).pointcut();
        ProxyPointcut proxyPointcut = new ProxyPointcut();
        proxyPointcut.setExpression(expression);
        Advice advice = (Advice) beanContainer.getBean(aspectClass);
        return new ProxyAdvisor(advice, proxyPointcut, order);
    }

    private List<ProxyAdvisor> createMatchProxies(List<ProxyAdvisor> proxyList, Class<?> targetClass) {
        Object targetBean = beanContainer.getBean(targetClass);
        return proxyList.stream()
                .filter(advisor -> advisor.getPointcut().matches(targetBean.getClass()))
                .sorted(Comparator.comparingInt(ProxyAdvisor::getOrder))
                .collect(Collectors.toList());
    }
}
