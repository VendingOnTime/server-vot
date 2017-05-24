package com.vendingontime.backend.middleware;

import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.config.inject.ConfigModule;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.utils.TokenGenerator;
import com.vendingontime.backend.utils.StringUtils;
import spark.Request;
import spark.Service;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
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
public class TokenEndpointProtector implements EndpointProtector, SparkMiddleware {

    public static final String AUTHORIZATION = "Authorization";
    public static final String LOGGED_IN_PERSON = "loggedInPerson";

    private final RESTContext context;
    private final TokenGenerator tokenGenerator;
    private final String tokenStrategyType;

    private String endpointUri;

    @Inject
    public TokenEndpointProtector(@Named(ConfigModule.TOKEN_STRATEGY_TYPE) String tokenStrategyType,
                                  RESTContext context, TokenGenerator tokenGenerator) {
        this.tokenStrategyType = tokenStrategyType;
        this.context = context;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public void protect(String uri) {
        this.endpointUri = uri;
        context.addMiddleware(this);
    }

    @Override
    public void configure(Service service) {
        if (endpointUri == null) throw new IllegalStateException("Endpoint must call protect first");
        service.before(endpointUri, (request, response) -> {

            if (request.requestMethod().equals("OPTIONS")) return;

            if (!fillRequestWithPersonIfAuthorized(request)) {
                service.halt(HttpResponse.StatusCode.UNAUTHORIZED, "Unauthorized");
            }
        });
    }

    public boolean fillRequestWithPersonIfAuthorized(Request req) {
        String authHeader = req.headers(AUTHORIZATION);
        Optional<String> possibleToken = getTokenFrom(authHeader);

        if (!possibleToken.isPresent()) return false;
        String token = possibleToken.get();

        Optional<Person> possiblePerson = tokenGenerator.recoverFrom(token);
        if (!possiblePerson.isPresent()) return false;

        req.attribute(LOGGED_IN_PERSON, possiblePerson.get());
        return true;
    }

    private Optional<String> getTokenFrom(String header) {
        if (StringUtils.isEmpty(header)) return Optional.empty();

        String[] headerParts = header.split(" ");
        if (headerParts.length != 2) return Optional.empty();

        String tokenStrategy = headerParts[0];
        String token = headerParts[1];

        if (!tokenStrategy.equals(tokenStrategyType)) return Optional.empty();
        return Optional.of(token);
    }
}
