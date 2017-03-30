package com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonCollisionException;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import javax.inject.Inject;

/**
 * Created by miguel on 13/3/17.
 */
public class SignUpService {

    private PersonRepository repository;

    @Inject
    public SignUpService(PersonRepository repository) {
        this.repository = repository;
    }

    public Person createSupervisor(SignUpData supervisorCandidate) throws BusinessLogicException {
        return createPerson(supervisorCandidate, PersonRole.SUPERVISOR);
    }

    private Person createPerson(SignUpData personCandidate, PersonRole role) {
        personCandidate.setRole(role);
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

        return person;
    }
}
