package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.AddMachineRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.models.bodymodels.machine.AddMachineData.INVALID_DESCRIPTION;
import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
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
public class AddMachineRouterTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private AddMachineService service;
    private ServiceResponse serviceResponse;
    private EndpointProtector protector;
    private AddMachineRouter router;
    private AddMachineData payload;
    private Machine machine;
    private Person person;
    private String stringifiedMachine;

    @Before
    public void setUp() throws Exception {
        service = mock(AddMachineService.class);
        serviceResponse = mock(ServiceResponse.class);
        protector = mock(EndpointProtector.class);

        router = new AddMachineRouter(serviceResponse, service, protector);

        payload = FixtureFactory.generateAddMachineData();
        machine = new Machine(payload);
        person = FixtureFactory.generateSupervisorWithCompany();
        stringifiedMachine = objectMapper.writeValueAsString(payload);
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        serviceResponse = null;
        protector = null;
        router = null;
        payload = null;
        machine = null;
        stringifiedMachine = null;
    }

    @Test
    public void addMachine_withValidData() {
        when(service.createMachine(any())).thenReturn(machine);

        router.addMachine(stringifiedMachine, person);

        verify(serviceResponse, times(1)).created(machine);
        verify(service, times(1)).createMachine(payload.setRequester(person));
    }

    @Test
    public void addMachine_withInvalidData() {
        String[] expectedErrors = new String[]{INVALID_DESCRIPTION};

        doThrow(new BusinessLogicException(expectedErrors))
                .when(service).createMachine(payload.setRequester(person));
        router.addMachine(stringifiedMachine, person);

        verify(service, times(1)).createMachine(payload);
        verify(serviceResponse, times(1)).badRequest(expectedErrors);
    }

    @Test
    public void addMachine_withEmptyJSON() {
        stringifiedMachine = "";

        router.addMachine(stringifiedMachine, person);

        verify(serviceResponse, never()).created(machine);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).createMachine(payload);
    }

    @Test
    public void addMachine_withInvalidJSONField() {
        stringifiedMachine = "{\"id\":\"1234\"}";

        router.addMachine(stringifiedMachine, person);

        verify(serviceResponse, never()).created(machine);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).createMachine(payload);
    }
}