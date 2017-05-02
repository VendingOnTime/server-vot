package com.vendingontime.backend.config.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.config.variables.ServerVariable;
import com.vendingontime.backend.initializers.DBInitializer;
import com.vendingontime.backend.initializers.RouteInitializer;
import com.vendingontime.backend.config.variables.MemoryServerConfig;
import com.vendingontime.backend.config.variables.ServerConfig;
import com.vendingontime.backend.initializers.SparkPluginInitializer;
import com.vendingontime.backend.initializers.sparkplugins.CORSPlugin;
import com.vendingontime.backend.initializers.sparkplugins.SparkPlugin;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.middleware.TokenEndpointProtector;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.repositories.*;

import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.*;

import com.vendingontime.backend.routes.utils.*;
import com.vendingontime.backend.services.*;
import com.vendingontime.backend.routes.LogInRouter;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.SparkRouter;
import com.vendingontime.backend.services.utils.*;
import spark.ResponseTransformer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class ConfigModule extends AbstractModule {

    public static final String RESPONSE_CONTENT_TYPE = "RESPONSE_CONTENT_TYPE";
    private static final String RESPONSE_CONTENT_TYPE_DEFAULT = "application/json";

    public static final String TOKEN_STRATEGY_TYPE = "TOKEN_TYPE";
    private static final String TOKEN_STRATEGY_TYPE_DEFAULT = "JWT";

    @Override
    protected void configure() {
        bindLiterals();
        bindCoreComponents();
        bindServiceUtils();
        bindRouterUtils();
        bindRepositories();
        bindServices();
        bindMiddleware();
        bindRoutes();
        bindPlugins();
    }

    private void bindLiterals() {
        bind(String.class)
                .annotatedWith(Names.named(RESPONSE_CONTENT_TYPE))
                .toInstance(RESPONSE_CONTENT_TYPE_DEFAULT);
        bind(String.class)
                .annotatedWith(Names.named(TOKEN_STRATEGY_TYPE))
                .toInstance(TOKEN_STRATEGY_TYPE_DEFAULT);
    }

    private void bindCoreComponents() {
        install(new JpaPersistModule(new MemoryServerConfig().getString(ServerVariable.DATA_SOURCE)));
        bind(DBInitializer.class).asEagerSingleton();
        bind(ServerConfig.class).to(MemoryServerConfig.class).in(Singleton.class);
        bind(RESTContext.class).in(Singleton.class);
        bind(RouteInitializer.class).in(Singleton.class);
        bind(SparkPluginInitializer.class).in(Singleton.class);
    }

    private void bindServiceUtils() {
        bind(TokenGenerator.class).to(JWTTokenGenerator.class);
        bind(PasswordEncryptor.class).to(DummyPasswordEncryptor.class);
        bind(AuthProvider.class).to(AuthProviderImpl.class);
    }

    private void bindRouterUtils() {
        bind(ResponseTransformer.class).to(JSONTransformer.class);
        bind(ServiceResponse.class).to(HttpResponse.class);
        bind(ResultFactory.class).to(RESTResultFactory.class);
    }

    private void bindRepositories() {
        bind(PersonRepository.class).to(JPAPersonRepository.class);
        bind(new TypeLiteral<Repository<Person>>(){}).to(JPAPersonRepository.class);

        bind(CompanyRepository.class).to(JPACompanyRepository.class);
        bind(new TypeLiteral<Repository<Company>>(){}).to(JPACompanyRepository.class);

        bind(MachineRepository.class).to(JPAMachineRepository.class);
        bind(new TypeLiteral<Repository<Machine>>(){}).to(JPAMachineRepository.class);
    }

    private void bindServices() {
        bind(SignUpService.class);
        bind(LogInService.class);
        bind(AddMachineService.class);
        bind(ListMachinesService.class);
        bind(RemoveMachineService.class);
        bind(EditMachineService.class);
        bind(GetMachineService.class);
        bind(EditPersonService.class);
    }

    private void bindMiddleware() {
        bind(EndpointProtector.class).to(TokenEndpointProtector.class);
    }

    private void bindRoutes() {
        Multibinder<SparkRouter> routerBinder = Multibinder.newSetBinder(binder(), SparkRouter.class);
        routerBinder.addBinding().to(SignUpRouter.class);
        routerBinder.addBinding().to(LogInRouter.class);
        routerBinder.addBinding().to(UserProfileRouter.class);
        routerBinder.addBinding().to(ListMachinesRouter.class);
        routerBinder.addBinding().to(AddMachineRouter.class);
        routerBinder.addBinding().to(RemoveMachineRouter.class);
        routerBinder.addBinding().to(EditMachineRouter.class);
        routerBinder.addBinding().to(GetMachineRouter.class);
    }

    private void bindPlugins() {
        Multibinder<SparkPlugin> sparkPluginBinder = Multibinder.newSetBinder(binder(), SparkPlugin.class);
        sparkPluginBinder.addBinding().to(CORSPlugin.class);
    }
}
