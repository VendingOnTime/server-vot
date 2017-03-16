package com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;
import com.vendingontime.backend.models.viewmodels.PersonPayload;
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
            PersonPayload personCandidate = mapper.readValue(requestBody, PersonPayload.class);

            return createUser(personCandidate);
        } catch (Exception ex) {
            ex.printStackTrace();
            //FIXME
            return null;
        }
    }

    private AppRoute createUser(PersonPayload personCandidate) {
        if(!personCandidate.isValid()) {
            // TODO Error
        }

        Person person = new Person(personCandidate);

        try {
            repository.create(person);
        } catch (PersonCollisionException ex) {
            // TODO Error
            response.badRequest()
        }

        return response.created(person);
    }
}
