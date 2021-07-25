package com.eric.projects.aop;

import com.eric.projects.aop.advice.Advice;
import com.eric.projects.aop.advice.AfterReturningAdvice;
import com.eric.projects.aop.advice.MethodBeforeAdvice;
import com.eric.projects.aop.advice.ThrowsAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProxyAdvisor {


    private Advice advice;

    private ProxyPointcut pointcut;

    /**
     * Execution order
     */
    private int order;

    public Object doProxy(AdviceChain adviceChain)
            throws Throwable {

        Object result = null;
        Class<?> targetClass = adviceChain.getTargetClass();
        Method method = adviceChain.getMethod();
        Object[] args = adviceChain.getArgs();

        try {
            if (advice instanceof MethodBeforeAdvice) {
                ((MethodBeforeAdvice) advice).before(targetClass, method, args);
            }

            // Attention: use $invokeSuper, target is the enhanced(original) object to get invoked
            result = adviceChain.doAdviceChain();

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
