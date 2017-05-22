package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.bodymodels.person.AddTechnicianData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.AddTechnicianRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.models.bodymodels.person.SignUpData.INVALID_DNI;
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
public class AddTechnicianRouterTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ServiceResponse serviceResponse;
    private EndpointProtector protector;
    private SignUpService service;
    private AddTechnicianRouter addTechnicianRouter;
    private AddTechnicianData technicianData;

    private Person supervisor;
    private Person technician;
    private String stringifiedPerson;

    @Before
    public void setUp() throws Exception {
        serviceResponse = mock(ServiceResponse.class);
        protector = mock(EndpointProtector.class);
        service = mock(SignUpService.class);
        addTechnicianRouter = new AddTechnicianRouter(serviceResponse, protector, service);

        technicianData = FixtureFactory.generateAddTechnicianData();
        supervisor = FixtureFactory.generateSupervisorWithCompany();
        technician = new Person(technicianData);
        stringifiedPerson = objectMapper.writeValueAsString(technicianData);
    }

    @After
    public void tearDown() throws Exception {
        serviceResponse = null;
        protector = null;
        service = null;
        addTechnicianRouter = null;
        technicianData = null;
        supervisor = null;
        technician = null;
        stringifiedPerson = null;
    }

    @Test
    public void addTechnician() {
        when(service.createTechnician(technicianData.setRequester(supervisor))).thenReturn(technician);
        addTechnicianRouter.addTechnician(stringifiedPerson, supervisor);

        verify(serviceResponse, times(1)).created(technician);
        verify(service, times(1)).createTechnician(technicianData);
    }

    @Test
    public void addTechnician_withInvalidData() {
        doThrow(new BusinessLogicException(new String[]{INVALID_DNI}))
                .when(service).createTechnician(technicianData.setRequester(supervisor));
        addTechnicianRouter.addTechnician(stringifiedPerson, supervisor);

        verify(service, times(1)).createTechnician(technicianData);
        verify(serviceResponse, times(1)).badRequest(any());
    }

    @Test
    public void addTechnician_withEmptyJSON() {
        stringifiedPerson = "";

        addTechnicianRouter.addTechnician(stringifiedPerson, supervisor);

        verify(serviceResponse, never()).created(technician);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).createTechnician(technicianData);
    }

    @Test
    public void addTechnician_withInvalidJSONField() {
        stringifiedPerson = "{\"id\":\"1234\"}";

        addTechnicianRouter.addTechnician(stringifiedPerson, supervisor);

        verify(serviceResponse, never()).created(technician);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).createTechnician(technicianData);
    }
}