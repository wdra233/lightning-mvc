package com.eric.projects.mvc;

import com.eric.projects.core.BeanContainer;
import com.eric.projects.mvc.annotation.RequestMapping;
import com.eric.projects.mvc.annotation.RequestMethod;
import com.eric.projects.mvc.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ControllerHandler {

    private Map<PathInfo, ControllerInfo> pathControllerMap = new ConcurrentHashMap<>();

    private BeanContainer beanContainer;

    public ControllerHandler() {
        beanContainer = BeanContainer.getInstance();
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        for (Class<?> clazz : classSet) {
            putPathController(clazz);
        }
    }

    public ControllerInfo getController(String requestMethod, String requestPath) {
        PathInfo pathInfo = new PathInfo(requestMethod, requestPath);
        return pathControllerMap.get(pathInfo);
    }

    private void putPathController(Class<?> clazz) {
        RequestMapping controllerRequest = clazz.getAnnotation(RequestMapping.class);
        String basePath = controllerRequest.value();
        Method[] controllerMethods = clazz.getDeclaredMethods();
        // 1. Iterate through all the methods within Controller
        for (Method method : controllerMethods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                // 2. Extract the alias name for the param as well as the param type
                Map<String, Class<?>> params = new HashMap<>();
                for (Parameter methodParam : method.getParameters()) {
                    RequestParam requestParam = methodParam.getAnnotation(RequestParam.class);
                    if (requestParam == null) {
                        throw new RuntimeException("param must be annotated with RequestParam");
                    }
                    params.put(requestParam.value(), methodParam.getType());
                }
                // 3. Get the RequestMapping annotation from this method
                RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                String methodPath = methodRequest.value();
                RequestMethod requestMethod = methodRequest.method();
                PathInfo pathInfo = new PathInfo(requestMethod.toString(), basePath + methodPath);
                if (pathControllerMap.containsKey(pathInfo)) {
                    log.error("Duplicate url: {}", pathInfo.getHttpPath());
                    throw new RuntimeException("Duplicate url registered");
                }
                // 4. Create ControllerInfo and save it into map
                ControllerInfo controllerInfo = new ControllerInfo(clazz, method, params);
                pathControllerMap.put(pathInfo, controllerInfo);
                log.info("Add Controller RequestMethod:{}, RequestPath:{}, Controller:{}, Method:{}",
                        pathInfo.getHttpMethod(), pathInfo.getHttpPath(),
                        controllerInfo.getControllerClass().getName(), controllerInfo.getInvokeMethod().getName());
            }
        }
    }
}
