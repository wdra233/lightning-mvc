package com.eric.projects.core;

import com.eric.projects.core.annotation.Component;
import com.eric.projects.core.annotation.Controller;
import com.eric.projects.core.annotation.Repository;
import com.eric.projects.core.annotation.Service;
import com.eric.projects.util.ClassUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {

    private volatile boolean isLoadBean = false;

    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION =
            Arrays.asList(Component.class, Controller.class, Repository.class, Service.class);

    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    public void loadBeans(String basePackage) {
        if (isLoadBean) {
            log.warn("bean already loaded");
            return;
        }

        synchronized (this) {
            Set<Class<?>> classSet = ClassUtil.getPackageClass(basePackage);
            classSet.stream().
                    filter(clazz -> {
                        for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                            if (clazz.isAnnotationPresent(annotation)) {
                                return true;
                            }
                        }
                        return false;
                    }).
                    forEach(clazz -> beanMap.put(clazz, ClassUtil.newInstance(clazz)));
            isLoadBean = true;
        }
    }

    public boolean isLoadBean() {
        return isLoadBean;
    }

    public Object getBean(Class<?> clazz) {
        return beanMap.getOrDefault(clazz, null);
    }

    public Set<Object> getBeans() {
        return (Set<Object>) beanMap.values();
    }

    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    public void removeBean(Class<?> clazz) {
        beanMap.remove(clazz);
    }

    public int size() {
        return beanMap.size();
    }

    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * Retrieve all classes info from the given annotation. eg, Component, Service..
     * @return
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        return beanMap.keySet().
                stream().
                filter(clazz -> clazz.isAnnotationPresent(annotation)).
                collect(Collectors.toSet());
    }

    /**
     * Retrieve all classes info from the given superClass
     * @param superClass
     * @return
     */
    public Set<Class<?>> getClassesBySuper(Class<?> superClass) {
        return beanMap.keySet().
                stream().
                filter(superClass::isAssignableFrom).
                filter(clazz -> !clazz.equals(superClass)).
                collect(Collectors.toSet());
    }

    /**
     * Apply singleton pattern
     */
    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

}
