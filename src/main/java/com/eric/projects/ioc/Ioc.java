package com.eric.projects.ioc;

import com.eric.projects.core.BeanContainer;
import com.eric.projects.ioc.annotation.Autowired;
import com.eric.projects.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
public class Ioc {

    private final BeanContainer beanContainer;

    public Ioc() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doIoc() {
        for (Class<?> clazz : beanContainer.getClasses()) {
            final Object targetBean = beanContainer.getBean(clazz);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // dependency injection
                if (field.isAnnotationPresent(Autowired.class)) {
                    final Class<?> fieldClass = field.getType();
                    Object fieldInstance = getClassInstance(fieldClass);
                    if (fieldInstance != null) {
                        ClassUtil.setField(field, targetBean, fieldInstance);
                    } else {
                        throw new RuntimeException("Cannot inject the target class: " + fieldClass.getName());
                    }
                }
            }
        }
    }

    private Object getClassInstance(final Class<?> fieldClass) {
        return Optional
                .ofNullable(beanContainer.getBean(fieldClass))
                .orElseGet(() -> {
                    Class<?> implementClass = getImplementClass(fieldClass);
                    if (implementClass != null) {
                        return beanContainer.getBean(implementClass);
                    }
                    return null;
                });
    }

    /**
     * Find the first child class that implemented the superClass
     * @param superClass
     * @return
     */
    private Class<?> getImplementClass(Class<?> superClass) {
        return beanContainer.getClassesBySuper(superClass)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
