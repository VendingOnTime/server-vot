package com.vendingontime.backend.services.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vendingontime.backend.config.variables.ServerConfig;
import com.vendingontime.backend.config.variables.ServerVariable;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.person.LogInData.BAD_LOGIN;

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
public class JWTTokenGenerator implements TokenGenerator {

    private static final String EMAIL_CLAIM = "email";

    private final ServerConfig config;
    private final PersonRepository repository;

    @Inject
    public JWTTokenGenerator(ServerConfig config, PersonRepository repository) {
        this.config = config;
        this.repository = repository;
    }

    @Override
    public String generateFrom(LogInData userData) {
        try {
            return JWT.create()
                    .withIssuer(config.getString(ServerVariable.JWT_ISSUER))
                    .withIssuedAt(new Date())
                    .withClaim(EMAIL_CLAIM, userData.getEmail())
                    .sign(getAlgorithm());
        } catch (UnsupportedEncodingException | JWTCreationException ex) {
            throw new BusinessLogicException(new String[]{BAD_LOGIN});
        }
    }

    @Override
    public Optional<Person> recoverFrom(String token) {
        try {
            JWTVerifier verifier = JWT.require(getAlgorithm())
                    .withIssuer(config.getString(ServerVariable.JWT_ISSUER))
                    .build();

            verifier.verify(token);

            JWT decode = JWT.decode(token);
            String email = decode.getClaim(EMAIL_CLAIM).asString();
            return repository.findByEmail(email);
        } catch (UnsupportedEncodingException | SignatureVerificationException | JWTDecodeException e) {
            return Optional.empty();
        }
    }

    private Algorithm getAlgorithm() throws UnsupportedEncodingException {
        return Algorithm.HMAC256(config.getString(ServerVariable.JWT_SECRET));
    }
}
