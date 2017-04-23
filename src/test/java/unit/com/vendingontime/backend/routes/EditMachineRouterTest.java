package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.EditMachineRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.EditMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.machine.AddMachineData.INVALID_MACHINE_DESCRIPTION;
import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

public class EditMachineRouterTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private EditMachineService service;
    private ServiceResponse serviceResponse;
    private EndpointProtector protector;
    private EditMachineRouter router;
    private EditMachineData payload;
    private Machine machine;
    private Person person;
    private String stringifiedMachine;

    @Before
    public void setUp() throws Exception {
        service = mock(EditMachineService.class);
        serviceResponse = mock(ServiceResponse.class);
        protector = mock(EndpointProtector.class);

        router = new EditMachineRouter(serviceResponse, service, protector);

        payload = FixtureFactory.generateEditMachineData();
        payload.setId("MACHINE_ID");
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
    public void editMachine_withValidData() {
        when(service.updateMachine(any())).thenReturn(Optional.ofNullable(machine));

        router.updateMachine(stringifiedMachine, person);

        verify(service, times(1))
                .updateMachine((EditMachineData) payload.setRequester(person));
        verify(serviceResponse, times(1)).ok(machine);
    }

    @Test
    public void editMachine_withInvalidData() {
        String[] expectedErrors = new String[]{ INVALID_MACHINE_DESCRIPTION };

        doThrow(new BusinessLogicException(expectedErrors))
                .when(service).updateMachine((EditMachineData) payload.setRequester(person));
        router.updateMachine(stringifiedMachine, person);

        verify(service, times(1)).updateMachine(payload);
        verify(serviceResponse, times(1)).badRequest(expectedErrors);
    }

    @Test
    public void editMachine_withNotExistingMachine_returnsNotFound() throws Exception {
        when(service.updateMachine(payload)).thenReturn(Optional.empty());

        router.updateMachine(stringifiedMachine, person);

        verify(serviceResponse, times(1)).notFound();
    }

    @Test
    public void editMachine_withEmptyJSON() {
        stringifiedMachine = "";

        router.updateMachine(stringifiedMachine, person);

        verify(serviceResponse, never()).ok(machine);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).updateMachine(payload);
    }

    @Test
    public void editMachine_withInvalidJSONField() {
        stringifiedMachine = "{\"invalid\":\"1234\"}";

        router.updateMachine(stringifiedMachine, person);

        verify(serviceResponse, never()).ok(machine);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).updateMachine(payload);
    }
}