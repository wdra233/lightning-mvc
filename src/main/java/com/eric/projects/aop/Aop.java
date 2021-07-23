package com.eric.projects.aop;

import com.eric.projects.aop.advice.Advice;
import com.eric.projects.aop.annotation.Aspect;
import com.eric.projects.core.BeanContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Aop {
    private BeanContainer beanContainer;

    public Aop() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doAop() {
        beanContainer.getClassesBySuper(Advice.class)
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(Aspect.class))
                .map(this::createProxyAdvisor)
                .forEach(proxyAdvisor -> beanContainer.getClasses()
                        .stream()
                        .filter(target -> !Advice.class.isAssignableFrom(target))
                        .filter(target -> !target.isAnnotationPresent(Aspect.class))
                        .forEach(target -> {
                            if(proxyAdvisor.getPointcut().matches(target)) {
                                Object proxyBean = ProxyCreator.createProxy(target, proxyAdvisor);
                                beanContainer.addBean(target, proxyBean);
                            }
                        })
                );
    }

    private ProxyAdvisor createProxyAdvisor(Class<?> aspectClass) {
        final String expression  = aspectClass.getAnnotation(Aspect.class).pointcut();
        ProxyPointcut proxyPointcut = new ProxyPointcut();
        proxyPointcut.setExpression(expression);
        Advice advice = (Advice) beanContainer.getBean(aspectClass);
        return new ProxyAdvisor(advice, proxyPointcut);
    }
}
