package com.vendingontime.backend.services;

import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import com.vendingontime.backend.services.utils.PasswordEncryptor;
import com.vendingontime.backend.services.utils.TokenGenerator;

import javax.inject.Inject;
import java.util.Optional;

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
public class LogInService {
    private PersonRepository repository;
    private PasswordEncryptor passwordEncryptor;
    private TokenGenerator tokenGenerator;

    @Inject
    public LogInService(PersonRepository repository, PasswordEncryptor passwordEncryptor, TokenGenerator tokenGenerator) {
        this.repository = repository;
        this.passwordEncryptor = passwordEncryptor;
        this.tokenGenerator = tokenGenerator;
    }

    public String authorizeUser(LogInData userData) throws BusinessLogicException {
        String[] signInErrors = userData.validate();

        if(signInErrors.length != 0) {
            throw new BusinessLogicException(signInErrors);
        }

        if (!checkProvidedData(userData)) {
            throw new BusinessLogicException(new String[]{BAD_LOGIN});
        }

        return tokenGenerator.generate(userData);
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
