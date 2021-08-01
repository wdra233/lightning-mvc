package com.sample;

import com.eric.projects.core.annotation.Controller;
import com.eric.projects.mvc.annotation.RequestMapping;
import com.eric.projects.mvc.annotation.ResponseBody;

@Controller
@RequestMapping
public class LightningController {
    @RequestMapping
    @ResponseBody
    public String hello() {
        return "hello world";
    }
}
