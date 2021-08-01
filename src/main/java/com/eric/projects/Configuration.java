package com.eric.projects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Configuration {

    private Class<?> bootClass;

    @Builder.Default
    private String resourcePath = "src/main/resources/";

    /**
     * jsp file directory
     */
    @Builder.Default
    private String viewPath = "/templates/";

    /**
     * static file directory
     */
    @Builder.Default
    private String assetPath = "/static/";

    @Builder.Default
    private int serverPort = 9090;

    /**
     * tomcat docBase directory
     */
    @Builder.Default
    private String docBase = "";

    /**
     * tomcat contextPath directory
     */
    @Builder.Default
    private String contextPath = "";

}
