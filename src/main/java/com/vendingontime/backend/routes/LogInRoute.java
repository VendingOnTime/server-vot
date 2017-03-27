package com.vendingontime.backend.routes;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.bodymodels.LogInData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.Response;
import com.vendingontime.backend.services.BusinessLogicException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.LogInData.BAD_LOGIN;

/**
 * Created by miguel on 3/26/17.
 */
public class LogInRoute {
    public final static String MALFORMED_JSON = "MALFORMED_JSON";

    private final ObjectMapper mapper = new ObjectMapper();
    private PersonRepository repository;
    private Response response;

    public LogInRoute(PersonRepository repository, Response response) {
        this.repository = repository;
        this.response = response;
    }

    public AppRoute post(String requestBody) {
        try {
            LogInData user = mapper.readValue(requestBody, LogInData.class);

            return authorizeUser(user);
        } catch (BusinessLogicException ex) {
            return response.badRequest(ex.getCauses());
        } catch (IOException ex) {
            return response.badRequest(MALFORMED_JSON);
        }
    }

    private AppRoute authorizeUser(LogInData userData) throws BusinessLogicException {
        String[] signInErrors = userData.validate();

        if(signInErrors.length != 0) {
            throw new BusinessLogicException(signInErrors);
        }

        if (!checkProvidedData(userData)) {
            throw new BusinessLogicException(new String[]{BAD_LOGIN});
        }

        return response.ok(generateAuthToken(userData));
    }

    private boolean checkProvidedData(LogInData userData) {
        Optional<Person> personFound = repository.findByEmail(userData.getEmail());

        if (personFound.isPresent()) {
            String userPassword = personFound.get().getPassword();

            if (userData.getPassword().equals(userPassword)) {
                return true;
            }
        }

        return false;
    }

    private String generateAuthToken(LogInData userData) {
        String authToken;

        try {
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
