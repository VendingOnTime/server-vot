package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.EditPersonRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.EditPersonService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
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
public class EditPersonRouterTest {
    private static final String PERSON_ID = "PERSON_ID";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EditPersonService service;
    private ServiceResponse serviceResponse;
    private EndpointProtector protector;
    private EditPersonRouter router;
    private EditPersonData payload;
    private Person person;
    private Person requester;
    private String stringifiedPerson;

    @Before
    public void setUp() throws Exception {
        service = mock(EditPersonService.class);
        serviceResponse = mock(ServiceResponse.class);
        protector = mock(EndpointProtector.class);

        router = new EditPersonRouter(serviceResponse, service, protector);

        payload = FixtureFactory.generateEditPersonData();
        requester = new Person(payload);
        person = FixtureFactory.generateSupervisorWithCompany();
        stringifiedPerson = objectMapper.writeValueAsString(payload);
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        serviceResponse = null;
        protector = null;
        router = null;
        payload = null;
        requester = null;
        person = null;
        stringifiedPerson = null;
    }

    @Test
    public void updatePerson_withValidData() {
        when(service.updatePerson(any())).thenReturn(Optional.of(requester));

        router.editProfile(PERSON_ID, stringifiedPerson, person);

        verify(service, times(1))
                .updatePerson((EditPersonData) payload.setId(PERSON_ID).setRequester(person));
        verify(serviceResponse, times(1)).ok(requester);
    }

    @Test
    public void updatePerson_withInvalidData() {
        String[] expectedErrors = new String[]{ EditPersonData.EMPTY_NAME };

        doThrow(new BusinessLogicException(expectedErrors))
                .when(service).updatePerson((EditPersonData) payload.setId(PERSON_ID).setRequester(person));
        router.editProfile(PERSON_ID, stringifiedPerson, person);

        verify(service, times(1)).updatePerson(payload.setId(PERSON_ID));
        verify(serviceResponse, times(1)).badRequest(expectedErrors);
    }

    @Test
    public void updatePerson_withNotExistingPerson_returnsNotFound() throws Exception {
        when(service.updatePerson(payload)).thenReturn(Optional.empty());

        router.editProfile(PERSON_ID, stringifiedPerson, person);

        verify(serviceResponse, times(1)).notFound();
    }

    @Test
    public void updatePerson_withEmptyJSON() {
        stringifiedPerson = "";

        router.editProfile(PERSON_ID, stringifiedPerson, person);

        verify(serviceResponse, never()).ok(any());
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).updatePerson(any());
    }

    @Test
    public void updatePerson_withInvalidJSONField() {
        stringifiedPerson = "{\"invalid\":\"1234\"}";

        router.editProfile(PERSON_ID, stringifiedPerson, person);

        verify(serviceResponse, never()).ok(any());
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).updatePerson(any());
    }
}