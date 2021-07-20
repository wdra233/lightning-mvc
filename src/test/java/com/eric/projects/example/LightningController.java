package com.eric.projects.example;

import com.eric.projects.core.annotation.Controller;
import com.eric.projects.ioc.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LightningController {

    @Autowired
    private LightningService service;

    public String hello() {
        return service.helloWord();
    }
}
