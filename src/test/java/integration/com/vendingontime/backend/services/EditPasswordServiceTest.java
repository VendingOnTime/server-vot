package integration.com.vendingontime.backend.services;

import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.person.EditPasswordData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.EditPasswordService;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

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

public class EditPasswordServiceTest extends IntegrationTest {

    @Inject private SignUpService signUpService;
    @Inject private EditPasswordService editPasswordService;

    @Inject private PersonRepository repository;

    @Test
    public void updatePerson() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        EditPasswordData editPasswordData = FixtureFactory.generateEditPasswordDataFrom(requester);

        final String newPassword = "NEW_PASSWORD";
        editPasswordData.setNewPassword(newPassword);

        editPasswordData.setRequester(requester);
        Optional<Person> possiblePerson = editPasswordService.updatePassword(editPasswordData);

        assertThat(possiblePerson.isPresent(), is(true));
        assertThat(possiblePerson.get().getPassword(), equalTo(newPassword));

        repository.delete(requester.getId());
    }
}