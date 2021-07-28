package com.eric.projects.mvc;

import com.alibaba.fastjson.JSON;
import com.eric.projects.core.BeanContainer;
import com.eric.projects.mvc.annotation.ResponseBody;
import com.eric.projects.mvc.bean.ModelAndView;
import com.eric.projects.util.CastUtil;
import com.eric.projects.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ResultRender {

    private BeanContainer beanContainer;

    public ResultRender() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * Execute methods within the Controller
     * @param req
     * @param resp
     * @param controllerInfo
     */
    public void invokeController(HttpServletRequest req, HttpServletResponse resp, ControllerInfo controllerInfo) {
        // 1. Extract all params from HttpServletRequest
        Map<String, String> requestParams = getRequestParams(req);

        // 2. Instantiate the method args
        List<Object> methodParams = instantiateMethodArgs(controllerInfo.getMethodParameter(), requestParams);

        Object controller = beanContainer.getBean(controllerInfo.getControllerClass());
        Method invokeMethod = controllerInfo.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result = null;
        // 3. Invoke the controller method via reflection
        try {

            if (methodParams.isEmpty()) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // resolve the returned result
        resultResolver(controllerInfo, result, req, resp);
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        // Extract request params for GET, POST
        request.getParameterMap().forEach((paramName, paramValues) -> {
            if (ValidateUtil.isNotEmpty(paramValues)) {
                paramMap.put(paramName, paramValues[0]);
            }
        });
        // TODO: Extract request params for Body, Path, Header
        return paramMap;
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
            }
            return value;
        }).collect(Collectors.toList());
    }

    private void resultResolver(ControllerInfo controllerInfo, Object result, HttpServletRequest req, HttpServletResponse resp) {
        if (result == null) {
            return;
        }

        boolean isJson = controllerInfo.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            try (PrintWriter writer = resp.getWriter()) {
                writer.write(JSON.toJSONString(result));
                writer.flush();
            } catch (IOException e) {
                log.error("forward request exception", e);
                // TODO: global Exception handling, 400...
            }
        } else {
            String path;
            if (result instanceof ModelAndView) {
                ModelAndView mv = (ModelAndView) result;
                path = mv.getView();
                Map<String, Object> model = mv.getModel();
                if (ValidateUtil.isNotEmpty(model)) {
                    for (Map.Entry<String, Object> entry : model.entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
            } else if (result instanceof String) {
                path = (String) result;
            } else {
                throw new RuntimeException("Illegal return type");
            }

            try {
                req.getRequestDispatcher("/templates/" + path).forward(req, resp);
            } catch (ServletException | IOException e) {
                log.error("Forward request failed");
                // TODO: Extract request params for Body, Path, Header
            }
        }
    }
}
