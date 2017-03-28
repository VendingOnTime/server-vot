package com.vendingontime.backend.config.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.config.variables.ServerVariable;
import com.vendingontime.backend.initializers.DBInitializer;
import com.vendingontime.backend.initializers.RouteInitializer;
import com.vendingontime.backend.config.variables.MemoryServerConfig;
import com.vendingontime.backend.config.variables.ServerConfig;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.repositories.Repository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.SparkRouter;
import com.vendingontime.backend.routes.TestRouter;
import com.vendingontime.backend.services.utils.DummyPasswordEncryptor;
import com.vendingontime.backend.services.utils.JWTTokenGenerator;
import com.vendingontime.backend.services.utils.PasswordEncryptor;
import com.vendingontime.backend.services.utils.TokenGenerator;

/**
 * Created by alberto on 27/3/17.
 */
public class ConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        bindCoreComponents();
        bindUtils();
        bindRepositories();
        bindRoutes();
    }

    private void bindCoreComponents() {
        install(new JpaPersistModule(new MemoryServerConfig().getString(ServerVariable.DATA_SOURCE)));
        bind(DBInitializer.class).asEagerSingleton();
        bind(ServerConfig.class).to(MemoryServerConfig.class).in(Singleton.class);
        bind(RESTContext.class).in(Singleton.class);
        bind(RouteInitializer.class).in(Singleton.class);
    }

    private void bindUtils() {
        bind(TokenGenerator.class).to(JWTTokenGenerator.class);
        bind(PasswordEncryptor.class).to(DummyPasswordEncryptor.class);
    }

    private void bindRepositories() {
        bind(PersonRepository.class);
        bind(new TypeLiteral<Repository<String, Person>>(){}).to(PersonRepository.class);
    }

    private void bindRoutes() {
        Multibinder<SparkRouter> routerBinder = Multibinder.newSetBinder(binder(), SparkRouter.class);
        routerBinder.addBinding().to(TestRouter.class);
    }
}
