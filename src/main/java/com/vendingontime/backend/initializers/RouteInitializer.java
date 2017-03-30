package com.vendingontime.backend.initializers;

import com.google.inject.Inject;
import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.routes.SparkRouter;

import java.util.Set;

/**
 * Created by alberto on 27/3/17.
 */
public class RouteInitializer {

    private final RESTContext context;
    private final Set<SparkRouter> routers;

    @Inject
    public RouteInitializer(RESTContext context, Set<SparkRouter> routers) {
        this.context = context;
        this.routers = routers;
    }

    public void setUp() {
        for (SparkRouter router : routers) {
            context.addRouter(router);
        }
    }
}
