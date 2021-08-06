package com.eric.projects.mvc.render;

import com.eric.projects.Lightning;
import com.eric.projects.mvc.RequestHandlerChain;
import com.eric.projects.mvc.bean.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ViewRender implements Render {
    private ModelAndView mv;

    public ViewRender(Object mv) {
        if (mv instanceof ModelAndView) {
            this.mv = (ModelAndView) mv;
        } else if (mv instanceof String) {
            this.mv = new ModelAndView().setView((String) mv);
        } else {
            throw new RuntimeException("Illegal return type");
        }
    }

    @Override
    public void render(RequestHandlerChain chain) throws Exception {
        HttpServletRequest request = chain.getRequest();
        HttpServletResponse response = chain.getResponse();
        String path = mv.getView();
        Map<String, Object> model = mv.getModel();
        model.forEach(request :: setAttribute);
        request.getRequestDispatcher(Lightning.getConfiguration().getViewPath() + path).forward(request, response);
    }
}
