package unit.com.vendingontime.backend.services.utils;

import com.vendingontime.backend.config.variables.ServerConfig;
import com.vendingontime.backend.config.variables.ServerVariable;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.utils.JWTTokenGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

public class JWTTokenGeneratorTest {
    private static final String EMAIL = "example@email.com";
    private static final String PASSWORD = "123456";

    private JWTTokenGenerator generator;

    private ServerConfig serverConfig;
    private LogInData logInData;
    private PersonRepository repository;

    @Before
    public void setUp() throws Exception {
        serverConfig = mock(ServerConfig.class);
        repository = mock(PersonRepository.class);

        generator = new JWTTokenGenerator(serverConfig, repository);

        logInData = new LogInData()
                .setEmail(EMAIL)
                .setPassword(PASSWORD);

        when(serverConfig.getString(ServerVariable.JWT_SECRET)).thenReturn("dummy");
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(new Person().setEmail(EMAIL)));
    }

    @After
    public void tearDown() throws Exception {
        serverConfig = null;
        logInData = null;
        generator = null;
    }

    @Test
    public void generateFrom() throws Exception {
        String token = generator.generateFrom(logInData);
        assertNotNull(token);
    }

    @Test
    public void recoverFrom() throws Exception {
        String token = generator.generateFrom(logInData);
        Optional<Person> person = generator.recoverFrom(token);

        verify(repository, times(1)).findByEmail(EMAIL);
        assertThat(person.isPresent(), is(true));
        assertThat(person.get().getEmail(), equalTo(EMAIL));
    }

    @Test
    public void recoverFrom_withInvalidToken() throws Exception {
        Optional<Person> noPerson = generator.recoverFrom("invalid");
        assertThat(noPerson.isPresent(), is(false));
    }
}