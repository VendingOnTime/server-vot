package com.vendingontime.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import com.vendingontime.backend.services.utils.PasswordEncryptor;
import com.vendingontime.backend.services.utils.TokenGenerator;

import java.io.IOException;
import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.person.LogInData.BAD_LOGIN;

/**
 * Created by miguel on 3/26/17.
 */
public class LogInService {
    public final static String MALFORMED_JSON = "MALFORMED_JSON";

    private final ObjectMapper mapper = new ObjectMapper();
    private JPAPersonRepository repository;
    private ServiceResponse serviceResponse;
    private PasswordEncryptor passwordEncryptor;
    private TokenGenerator tokenGenerator;

    public LogInService(JPAPersonRepository repository, ServiceResponse serviceResponse, PasswordEncryptor passwordEncryptor, TokenGenerator tokenGenerator) {
        this.repository = repository;
        this.serviceResponse = serviceResponse;
        this.passwordEncryptor = passwordEncryptor;
        this.tokenGenerator = tokenGenerator;
    }

    public AppRoute post(String requestBody) {
        try {
            LogInData user = mapper.readValue(requestBody, LogInData.class);

            return authorizeUser(user);
        } catch (BusinessLogicException ex) {
            return serviceResponse.badRequest(ex.getCauses());
        } catch (IOException ex) {
            return serviceResponse.badRequest(MALFORMED_JSON);
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

        return serviceResponse.ok(tokenGenerator.generate(userData));
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
