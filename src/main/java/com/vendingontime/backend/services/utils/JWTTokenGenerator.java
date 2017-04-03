package com.vendingontime.backend.services.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.vendingontime.backend.models.bodymodels.person.LogInData;

import java.io.UnsupportedEncodingException;

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
    @Override
    public String generate(LogInData userData) {
        String authToken;

        try {
            //TODO Change secret with env variable
            Algorithm algorithm = Algorithm.HMAC256("secret");
            authToken = JWT.create()
                    .withClaim("email", userData.getEmail())
                    .sign(algorithm);

            return authToken;
        } catch (UnsupportedEncodingException | JWTCreationException ex) {
            throw new BusinessLogicException(new String[]{BAD_LOGIN});
        }
    }
}
