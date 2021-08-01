package com.eric.projects.mvc.server;

public interface Server {

    /**
     * Start server
     * @throws Exception
     */
    void startServer() throws Exception;

    /**
     * Stop server
     * @throws Exception
     */
    void stopServer() throws Exception;
}
