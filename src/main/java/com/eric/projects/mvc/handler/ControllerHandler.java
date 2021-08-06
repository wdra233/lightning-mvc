package com.eric.projects.mvc.handler;

import com.eric.projects.core.BeanContainer;
import com.eric.projects.mvc.ControllerInfo;
import com.eric.projects.mvc.PathInfo;
import com.eric.projects.mvc.RequestHandlerChain;
import com.eric.projects.mvc.annotation.RequestMapping;
import com.eric.projects.mvc.annotation.RequestParam;
import com.eric.projects.mvc.annotation.ResponseBody;
import com.eric.projects.mvc.render.JsonRender;
import com.eric.projects.mvc.render.NotFoundRender;
import com.eric.projects.mvc.render.Render;
import com.eric.projects.mvc.render.ViewRender;
import com.eric.projects.util.CastUtil;
import com.eric.projects.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ControllerHandler implements Handler {

    private Map<PathInfo, ControllerInfo> pathControllerMap = new ConcurrentHashMap<>();

    private BeanContainer beanContainer;

    public ControllerHandler() {
        beanContainer = BeanContainer.getInstance();

        Set<Class<?>> mappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        initPathControllerMap(mappingSet);
    }

    private void initPathControllerMap(Set<Class<?>> mappingSet) {
        mappingSet.forEach(this::addPathController);
    }

    private void addPathController(Class<?> clazz) {
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        String basePath = requestMapping.value();
        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                String methodPath = methodRequest.value();
                if (!methodPath.startsWith("/")) {
                    methodPath = "/" + methodPath;
                }
                String url = basePath + methodPath;
                if (url.endsWith("//")) {
                    url = url.substring(url.length() - 1);
                }
                Map<String, Class<?>> methodParams = this.getMethodParams(method);
                String httpMethod = String.valueOf(methodRequest.method());
                PathInfo pathInfo = new PathInfo(httpMethod, url);
                if (pathControllerMap.containsKey(pathInfo)) {
                    log.warn("url :{} duplicate url", pathInfo.getHttpPath());
                }
                ControllerInfo controllerInfo = new ControllerInfo(clazz, method, methodParams);
                pathControllerMap.put(pathInfo, controllerInfo);
                log.info("mapped:[{},method=[{}]] controller:[{}@{}]",
                        pathInfo.getHttpPath(), pathInfo.getHttpMethod(),
                        controllerInfo.getControllerClass().getName(), controllerInfo.getInvokeMethod().getName());
            }

        }
    }

    private Map<String, Class<?>> getMethodParams(Method method) {
        Map<String, Class<?>> map = new HashMap<>();
        for (Parameter parameter : method.getParameters()) {
            RequestParam param = parameter.getAnnotation(RequestParam.class);

            if (null == param) {
                throw new RuntimeException("A method param with @RequestParam is required");
            }
            map.put(param.value(), parameter.getType());
        }
        return map;
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        // Extract request params from GET and POST methods
        request.getParameterMap().forEach((paramName, paramValues) -> {
            if (ValidateUtil.isNotEmpty(paramValues)) {
                paramMap.put(paramName, paramValues[0]);
            }
        });
        // TODO: Extract request params for Body, Path, Header
        return paramMap;
    }

    @Override
    public boolean handle(final RequestHandlerChain chain) throws Exception {
        String method = chain.getRequestMethod();
        String path = chain.getRequestPath();

        ControllerInfo controllerInfo = pathControllerMap.get(new PathInfo(method, path));
        if (controllerInfo == null) {
            chain.setRender(new NotFoundRender());
            return false;
        }

        Object result = invokeController(controllerInfo, chain.getRequest());
        setRender(result, controllerInfo, chain);
        return true;

    }

    private Object invokeController(ControllerInfo controllerInfo, HttpServletRequest request) {
        Map<String, String> requestParams = getRequestParams(request);
        List<Object> methodParams = instantiateMethodArgs(controllerInfo.getMethodParameter(), requestParams);

        Object controller = beanContainer.getBean(controllerInfo.getControllerClass());
        Method invokeMethod = controllerInfo.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result;
        try {
            if (methodParams.isEmpty()) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private List<Object> instantiateMethodArgs(Map<String, Class<?>> methodParams, Map<String, String> requestParams) {
        return methodParams.keySet().stream().map(paramName -> {
            Class<?> type = methodParams.get(paramName);
            String requestValue = requestParams.get(paramName);
            Object value;
            if (requestValue == null) {
                value = CastUtil.primitiveNull(type);
            } else {
                value = CastUtil.convert(type, requestValue);
                // TODO: Instantiate non-native parameters
            }
            return value;
        }).collect(Collectors.toList());
    }

    private void setRender(Object result, ControllerInfo controllerInfo, RequestHandlerChain handlerChain) {
        if (null == result) {
            return;
        }
        Render render;
        boolean isJson = controllerInfo.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            render = new JsonRender(result);
        } else {
            render = new ViewRender(result);
        }
        handlerChain.setRender(render);
    }
}
