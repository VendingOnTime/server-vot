package com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.person.AddTechnicianData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonCollisionException;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import javax.inject.Inject;

/**
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
// FIXME: 11/05/2017 Is hard to find this class, it should be splitted in two
public class SignUpService extends AbstractService {

    private final PersonRepository repository;
    private final CompanyRepository companyRepository;
    private final AuthProvider authProvider;

    @Inject
    public SignUpService(PersonRepository repository, CompanyRepository companyRepository, AuthProvider authProvider) {
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.authProvider = authProvider;
    }

    public Person createSupervisor(SignUpData supervisorCandidate) throws BusinessLogicException {
        Person supervisor = createPerson(supervisorCandidate, PersonRole.SUPERVISOR);

        Company company = companyRepository.create(new Company());

        Person savedSupervisor = repository.findById(supervisor.getId()).get();
        company.setOwner(savedSupervisor);
        company.addWorker(savedSupervisor);

        companyRepository.update(company);

        return savedSupervisor;
    }

    public Person createTechnician(AddTechnicianData addTechnicianData) throws BusinessLogicException {
        if (!authProvider.canModify(addTechnicianData.getRequester(), addTechnicianData.getRequester().getCompany()))
            throw new BusinessLogicException(new String[]{INSUFFICIENT_PERMISSIONS});

        Person technician = createPerson(addTechnicianData, PersonRole.TECHNICIAN);

        Company company = addTechnicianData.getRequester().getCompany();

        Person savedTechnician = repository.findById(technician.getId()).get();
        company.addWorker(savedTechnician);

        companyRepository.update(company);

        return savedTechnician;
    }

    private Person createPerson(SignUpData personCandidate, PersonRole role) {
        personCandidate.setRole(role);

        String[] validationErrors = personCandidate.validate();
        if(validationErrors.length != 0) {
            throw new BusinessLogicException(validationErrors);
        }

        Person person = new Person(personCandidate);

        try {
            return repository.create(person);
        } catch (PersonCollisionException ex) {
            throw new BusinessLogicException(ex.getCauses());
        }
    }
}
