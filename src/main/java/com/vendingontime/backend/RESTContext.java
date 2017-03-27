package com.vendingontime.backend;

import com.google.inject.Inject;
import com.vendingontime.backend.config.variables.ServerConfig;
import com.vendingontime.backend.config.variables.ServerVariable;
import com.vendingontime.backend.routes.SparkRouter;
import spark.Service;

/**
 * Created by alberto on 27/3/17.
 */
public class RESTContext {

    private final Service http;

    @Inject
    public RESTContext(ServerConfig config) {
        this.http = Service.ignite()
                .port(config.getInt(ServerVariable.PORT));
    }

    public void addRouter(SparkRouter router) {
        router.configure(http);
    }

    public void stop() {
        http.stop();
    }
}
