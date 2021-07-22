package com.eric.projects.ioc;

import com.eric.projects.core.BeanContainer;
import com.eric.projects.example.LightningController;
import org.junit.Assert;
import org.junit.Test;

public class IocTest {

    @Test
    public void test_doIoc() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.eric.projects.example");
        new Ioc().doIoc();
        final LightningController controller = (LightningController) beanContainer.getBean(LightningController.class);
        controller.hello();
    }

}