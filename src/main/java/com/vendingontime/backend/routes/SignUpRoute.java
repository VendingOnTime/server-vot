package com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.Response;

/**
 * Created by miguel on 13/3/17.
 */
public class SignUpRoute {
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
        } catch (Exception ex) {
            ex.printStackTrace();
            //FIXME
            return null;
        }
    }

    private AppRoute createUser(SignUpData personCandidate) {
        String[] signUpErrors = personCandidate.validate();
        if(signUpErrors.length != 0) {
            response.badRequest(signUpErrors);
        }

        Person person = new Person(personCandidate);

        try {
            repository.create(person);
        } catch (PersonCollisionException ex) {
            response.badRequest(ex.getCauses());
        }

        return response.created(person);
    }
}
