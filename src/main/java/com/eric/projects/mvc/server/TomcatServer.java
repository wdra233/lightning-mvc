package com.eric.projects.mvc.server;

import com.eric.projects.Configuration;
import com.eric.projects.YW;
import com.eric.projects.mvc.DispatchServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.jasper.servlet.JspServlet;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;

@Slf4j
public class TomcatServer implements Server {

    private Tomcat tomcat;

    public TomcatServer() {
        this(YW.getConfiguration());
    }

    public TomcatServer(Configuration configuration) {
        try {
            this.tomcat = new Tomcat();
            tomcat.setBaseDir(configuration.getDocBase());
            tomcat.setPort(configuration.getServerPort());

            File root = getRootFolder();
            File webContentFolder = new File(root.getAbsolutePath(), configuration.getResourcePath());
            if (!webContentFolder.exists()) {
                webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
            }

            log.info("Tomcat:configuring app with basedir: [{}]", webContentFolder.getAbsolutePath());
            StandardContext ctx = (StandardContext) tomcat.addWebapp(configuration.getContextPath(), webContentFolder.getAbsolutePath());
            ctx.setParentClassLoader(this.getClass().getClassLoader());

            WebResourceRoot resources = new StandardRoot(ctx);
            ctx.setResources(resources);

            // Adding jspServlet，defaultServlet, and dispatcherServlet
            // Configure tomcat server
            tomcat.addServlet("", "jspServlet", new JspServlet()).setLoadOnStartup(3);
            tomcat.addServlet("", "defaultServlet", new DefaultServlet()).setLoadOnStartup(1);
            tomcat.addServlet("", "dispatcherServlet", new DispatchServlet()).setLoadOnStartup(0);
            ctx.addServletMappingDecoded("/templates/" + "*", "jspServlet");
            ctx.addServletMappingDecoded("/static/" + "*", "defaultServlet");
            ctx.addServletMappingDecoded("/*", "dispatcherServlet");

        } catch (Exception e) {
            log.error("Failed to initiate tomcat", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startServer() throws Exception {
        tomcat.start();
        String address = tomcat.getServer().getAddress();
        int port = tomcat.getConnector().getPort();
        log.info("local address: http://{}:{}", address, port);
        tomcat.getServer().await();
    }

    @Override
    public void stopServer() throws Exception {
        tomcat.stop();
    }

    private File getRootFolder() {
        File root;
        try {
            String runningJarPath = this.getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath()
                    .replace("\\\\", "/");
            int lastIndexOf = runningJarPath.lastIndexOf("/target/");
            if (lastIndexOf < 0) {
                root = new File("");
            } else {
                root = new File(runningJarPath.substring(0, lastIndexOf));
            }
            log.info("Tomcat:application resolved root folder: [{}]", root.getAbsolutePath());
            return root;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
