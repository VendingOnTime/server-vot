package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.models.bodymodels.person.SignUpData.INVALID_DNI;
import static com.vendingontime.backend.routes.SignUpRouter.MALFORMED_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
public class SignUpRouterTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private SignUpService service;
    private ServiceResponse serviceResponse;
    private SignUpRouter signUp;
    private SignUpData payload;

    private Person person;
    private String stringifiedPerson;

    @Before
    public void setUp() throws Exception {
        service = mock(SignUpService.class);
        serviceResponse = mock(ServiceResponse.class);
        signUp = new SignUpRouter(service, serviceResponse);

        payload = FixtureFactory.generateSignUpData();
        person = new Person(payload);
        stringifiedPerson = objectMapper.writeValueAsString(payload);
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        serviceResponse = null;
        signUp = null;
        payload = null;
        stringifiedPerson = null;
    }

    @Test
    public void signUpSupervisor() {
        when(service.createSupervisor(payload)).thenReturn(person);
        signUp.signUpSupervisor(stringifiedPerson);

        verify(serviceResponse, times(1)).created(person);
        verify(service, times(1)).createSupervisor(payload);
    }

    @Test
    public void signUpSupervisor_withInvalidData() {
        doThrow(new BusinessLogicException(new String[]{INVALID_DNI}))
                .when(service).createSupervisor(payload);
        signUp.signUpSupervisor(stringifiedPerson);

        verify(service, times(1)).createSupervisor(payload);
        verify(serviceResponse, times(1)).badRequest(any());
    }

    @Test
    public void signUpSupervisor_withEmptyJSON() {
        stringifiedPerson = "";

        signUp.signUpSupervisor(stringifiedPerson);

        verify(serviceResponse, never()).created(person);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).createSupervisor(payload);
    }

    @Test
    public void signUpSupervisor_withInvalidJSONField() {
        stringifiedPerson = "{\"id\":\"1234\"}";

        signUp.signUpSupervisor(stringifiedPerson);

        verify(serviceResponse, never()).created(person);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).createSupervisor(payload);
    }
}