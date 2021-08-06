package com.eric.projects.mvc.render;

import com.alibaba.fastjson.JSON;
import com.eric.projects.mvc.RequestHandlerChain;

import java.io.PrintWriter;

public class JsonRender implements Render {

    private Object jsonData;

    public JsonRender(Object jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public void render(RequestHandlerChain chain) throws Exception {
        chain.getResponse().setContentType("application/json");
        chain.getResponse().setCharacterEncoding("UTF-8");

        try (PrintWriter writer = chain.getResponse().getWriter()) {
            writer.write(JSON.toJSONString(jsonData));
            writer.flush();
        }
    }
}
