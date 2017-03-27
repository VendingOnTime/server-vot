package com.vendingontime.backend.config.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.initializers.RouteInitializer;
import com.vendingontime.backend.config.variables.MemoryServerConfig;
import com.vendingontime.backend.config.variables.ServerConfig;
import com.vendingontime.backend.routes.SparkRouter;
import com.vendingontime.backend.routes.TestRouter;

/**
 * Created by alberto on 27/3/17.
 */
public class ConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        bindCoreComponents();
        bindRoutes();
    }

    private void bindCoreComponents() {
        bind(ServerConfig.class).to(MemoryServerConfig.class).in(Singleton.class);
        bind(RESTContext.class).in(Singleton.class);
        bind(RouteInitializer.class).in(Singleton.class);
    }

    private void bindRoutes() {
        Multibinder<SparkRouter> routerBinder = Multibinder.newSetBinder(binder(), SparkRouter.class);
        routerBinder.addBinding().to(TestRouter.class);
    }
}
