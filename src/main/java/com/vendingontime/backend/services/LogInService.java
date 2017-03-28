package com.vendingontime.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.bodymodels.LogInData;
import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.Response;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import com.vendingontime.backend.services.utils.PasswordEncryptor;
import com.vendingontime.backend.services.utils.TokenGenerator;

import java.io.IOException;
import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.LogInData.BAD_LOGIN;

/**
 * Created by miguel on 3/26/17.
 */
public class LogInService {
    public final static String MALFORMED_JSON = "MALFORMED_JSON";

    private final ObjectMapper mapper = new ObjectMapper();
    private JPAPersonRepository repository;
    private Response response;
    private PasswordEncryptor passwordEncryptor;
    private TokenGenerator tokenGenerator;

    public LogInService(JPAPersonRepository repository, Response response, PasswordEncryptor passwordEncryptor, TokenGenerator tokenGenerator) {
        this.repository = repository;
        this.response = response;
        this.passwordEncryptor = passwordEncryptor;
        this.tokenGenerator = tokenGenerator;
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

        return response.ok(tokenGenerator.generate(userData));
    }

    private boolean checkProvidedData(LogInData userData) {
        Optional<Person> personFound = repository.findByEmail(userData.getEmail());

        if (personFound.isPresent()) {
            String userPassword = personFound.get().getPassword();

            if (passwordEncryptor.check(userPassword, userData.getPassword())) {
                return true;
            }
        }

        return false;
    }
}
