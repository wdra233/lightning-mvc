package com.eric.projects.aop;

import com.eric.projects.aop.advice.Advice;
import com.eric.projects.aop.advice.AfterReturningAdvice;
import com.eric.projects.aop.advice.MethodBeforeAdvice;
import com.eric.projects.aop.advice.ThrowsAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProxyAdvisor {

    private Advice advice;

    public Object doProxy(Object target, Class<?> targetClass, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {

        Object result = null;

        try {
            if (advice instanceof MethodBeforeAdvice) {
                ((MethodBeforeAdvice) advice).before(targetClass, method, args);
            }

            // Attention: use $invokeSuper, target is the enhanced(original) object to get invoked
            result = methodProxy.invokeSuper(target, args);

            if (advice instanceof AfterReturningAdvice) {
                ((AfterReturningAdvice) advice).afterReturning(targetClass, method, args);
            }
        } catch (Throwable e) {
            if (advice instanceof ThrowsAdvice) {
                ((ThrowsAdvice) advice).afterThrowing(targetClass, method, args, e);
            } else {
                throw new Throwable(e);
            }
        }

        return result;

    }
}
