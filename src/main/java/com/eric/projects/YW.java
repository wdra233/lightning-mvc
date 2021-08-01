package com.eric.projects;

import com.eric.projects.aop.Aop;
import com.eric.projects.core.BeanContainer;
import com.eric.projects.ioc.Ioc;
import com.eric.projects.mvc.server.Server;
import com.eric.projects.mvc.server.TomcatServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class YW {

    /**
     * Global configuration
     */
    @Getter
    private static Configuration configuration = Configuration.builder().build();

    @Getter
    private static Server server;

    public static void run(Class<?> bootClass) {
        run(Configuration.builder().bootClass(bootClass).build());
    }

    public static void run(Class<?> bootClass, int port) {
        run(Configuration.builder().bootClass(bootClass).serverPort(port).build());
    }


    public static void run(Configuration configuration) {
        new YW().start(configuration);
    }

    private void start(Configuration configuration) {
        try {
            YW.configuration = configuration;
            String basePackage = configuration.getBootClass().getPackage().getName();
            BeanContainer.getInstance().loadBeans(basePackage);

            new Aop().doAop();
            new Ioc().doIoc();

            server = new TomcatServer(configuration);
            server.startServer();
        } catch (Exception e) {
            log.error("Failed to boot up YW", e);
        }
    }
}
