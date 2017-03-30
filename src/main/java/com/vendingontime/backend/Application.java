package com.vendingontime.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vendingontime.backend.config.inject.ConfigModule;
import com.vendingontime.backend.initializers.RouteInitializer;
import com.vendingontime.backend.initializers.SparkPluginInitializer;

/**
 * Created by alberto on 7/3/17.
 */
public class Application {

    public static void main(String[] args) {
        new Application().start();
    }

    private void start() {
        Injector injector = Guice.createInjector(new ConfigModule());

        injector.getInstance(RouteInitializer.class).setUp();
        injector.getInstance(SparkPluginInitializer.class).setUp();
    }
}
