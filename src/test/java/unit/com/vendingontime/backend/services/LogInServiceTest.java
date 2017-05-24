package unit.com.vendingontime.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import com.vendingontime.backend.services.utils.PasswordEncryptor;
import com.vendingontime.backend.services.utils.TokenGenerator;
import org.junit.*;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.person.LogInData.BAD_LOGIN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

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
public class LogInServiceTest {
    private static final String EMAIL = "username@test.com";
    private static final String PASSWORD = "PASSWORD";

    private JPAPersonRepository repository;
    private PasswordEncryptor passwordEncryptor;
    private TokenGenerator tokenGenerator;
    private LogInService logInService;
    private LogInData logInData;

    @Before
    public void setUp() throws Exception {
        repository = mock(JPAPersonRepository.class);
        passwordEncryptor = mock(PasswordEncryptor.class);
        tokenGenerator = mock(TokenGenerator.class);

        logInService = new LogInService(repository, passwordEncryptor, tokenGenerator);

        logInData = new LogInData();
        logInData.setEmail(EMAIL);
        logInData.setPassword(PASSWORD);

        Person person = new Person().setEmail(EMAIL).setPassword(PASSWORD);

        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(logInData.getEmail())).thenReturn(Optional.of(person));

        when(passwordEncryptor.check(anyString(), anyString())).thenReturn(false);
        when(passwordEncryptor.check(PASSWORD, PASSWORD)).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        passwordEncryptor = null;
        tokenGenerator = null;
        logInService = null;
        logInData = null;
    }

    @Test
    public void authorizeUser() {
        String generatedToken = "tokenGenerated";
        when(tokenGenerator.generateFrom(logInData)).thenReturn(generatedToken);

        String token = logInService.authorizeUser(logInData);

        verify(repository, times(1)).findByEmail(logInData.getEmail());
        verify(passwordEncryptor, times(1)).check(PASSWORD, logInData.getPassword());
        verify(tokenGenerator, times(1)).generateFrom(logInData);
        assertEquals(generatedToken, token);
    }

    @Test
    public void post_withNonExistingEmail() throws JsonProcessingException {
        logInData.setEmail("nonExisting@mail.com");

        try {
            logInService.authorizeUser(logInData);
            fail();
        } catch (BusinessLogicException e) {
            verify(repository, times(1)).findByEmail(logInData.getEmail());
            verify(passwordEncryptor, never()).check(anyString(), anyString());
            verify(tokenGenerator, never()).generateFrom(any());
            assertArrayEquals(new String[]{BAD_LOGIN}, e.getCauses());
        }
    }

    @Test
    public void post_withIncorrectPassword() throws JsonProcessingException {
        String incorrectPwd = "incorrectPwd";
        logInData.setPassword(incorrectPwd);

        try {
            logInService.authorizeUser(logInData);
            fail();
        } catch (BusinessLogicException e) {
            verify(repository, times(1)).findByEmail(logInData.getEmail());
            verify(passwordEncryptor, times(1)).check(PASSWORD, incorrectPwd);
            verify(tokenGenerator, never()).generateFrom(any());
            assertArrayEquals(new String[]{BAD_LOGIN}, e.getCauses());
        }
    }

    @Test
    public void post_withIncorrectData() throws JsonProcessingException {
        logInData.setEmail("nonExistingmail.com").setPassword("incorrectPwd");

        try {
            logInService.authorizeUser(logInData);
            fail();
        } catch (BusinessLogicException e) {
            verify(repository, never()).findByEmail(anyString());
            verify(passwordEncryptor, never()).check(anyString(), anyString());
            verify(tokenGenerator, never()).generateFrom(any());
            assertArrayEquals(new String[]{BAD_LOGIN}, e.getCauses());
        }
    }
}
