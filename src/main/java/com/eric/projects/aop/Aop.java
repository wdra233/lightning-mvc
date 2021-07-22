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
                .forEach(clazz -> {
                    final Advice advice = (Advice) beanContainer.getBean(clazz);
                    final Aspect aspect = clazz.getAnnotation(Aspect.class);
                    beanContainer.getClassesByAnnotation(aspect.target())
                            .stream()
                            .filter(target -> !Advice.class.isAssignableFrom(target))
                            .filter(target -> !target.isAnnotationPresent(Aspect.class))
                            .forEach(target -> {
                                ProxyAdvisor proxyAdvisor = new ProxyAdvisor(advice);
                                Object proxyBean = ProxyCreator.createProxy(target, proxyAdvisor);
                                beanContainer.addBean(target, proxyBean);
                            });
                });
    }
}
