package com.eric.projects.aop;

import com.eric.projects.core.BeanContainer;
import com.eric.projects.example.LightningController;
import com.eric.projects.ioc.Ioc;
import org.junit.Test;

import static org.junit.Assert.*;

public class AopTest {

    @Test
    public void test_doAop() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.eric.projects.example");
        // doAop() must happen prior to doIoc()
        new Aop().doAop();
        new Ioc().doIoc();
        LightningController controller = (LightningController) beanContainer.getBean(LightningController.class);
        controller.hello();
//        controller.helloForAspect();
    }
}