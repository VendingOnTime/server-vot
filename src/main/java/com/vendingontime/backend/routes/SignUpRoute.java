package com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.Response;

import com.vendingontime.backend.services.BusinessLogicException;

import java.io.IOException;

/**
 * Created by miguel on 13/3/17.
 */
public class SignUpRoute {
    public final static String MALFORMED_JSON = "MALFORMED_JSON";

    private final ObjectMapper mapper = new ObjectMapper();
    private PersonRepository repository;
    private Response response;

    public SignUpRoute(PersonRepository repository, Response response) {
        this.repository = repository;
        this.response = response;
    }

    public AppRoute post(String requestBody) {
        try {
            SignUpData personCandidate = mapper.readValue(requestBody, SignUpData.class);

            return createUser(personCandidate);
        } catch (BusinessLogicException ex) {
            return response.badRequest(ex.getCauses());
        } catch (IOException ex) {
            return response.badRequest(MALFORMED_JSON);
        }
    }

    private AppRoute createUser(SignUpData personCandidate) throws BusinessLogicException {
        String[] signUpErrors = personCandidate.validate();
        if(signUpErrors.length != 0) {
            throw new BusinessLogicException(signUpErrors);
        }

        Person person = new Person(personCandidate);

        try {
            repository.create(person);
        } catch (PersonCollisionException ex) {
            throw new BusinessLogicException(ex.getCauses());
        }

        return response.created(person);
    }
}
