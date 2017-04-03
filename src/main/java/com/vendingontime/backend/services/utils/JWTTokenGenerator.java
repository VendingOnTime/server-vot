package com.vendingontime.backend.services.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.vendingontime.backend.models.bodymodels.person.LogInData;

import java.io.UnsupportedEncodingException;

import static com.vendingontime.backend.models.bodymodels.person.LogInData.BAD_LOGIN;

/**
 * Created by miguel on 28/3/17.
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
