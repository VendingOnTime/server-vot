package com.vendingontime.backend.config;

import static spark.Spark.port;

/**
 * Created by miguel on 13/3/17.
 */
public class ServerConfig {
    private final static String PORT_ENV_VAR = "PORT";
    private final static int DEFAULT_PORT = 8080;


    public static void config() {
        ServerConfig serverConfig = new ServerConfig();

        serverConfig.configPort();
    }

    private void configPort() {
        String envPort = System.getenv(PORT_ENV_VAR);
        int port = envPort != null ? Integer.parseInt(envPort) : DEFAULT_PORT;

        port(port);
    }
}
