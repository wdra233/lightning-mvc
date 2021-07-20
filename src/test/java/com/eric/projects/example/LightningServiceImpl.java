package com.eric.projects.example;

import com.eric.projects.core.annotation.Service;

@Service
public class LightningServiceImpl implements LightningService {
    @Override
    public String helloWord() {
        return "hello World";
    }
}
