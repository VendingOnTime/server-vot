package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.AssignMaintainerRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.AssignMaintainerService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData.EMPTY_TECHNICIAN_ID;
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

public class AssignMaintainerRouterTest {

    private static final String MACHINE_ID = "MACHINE_ID";
    private static final String TECHNICIAN_ID = "TECHNICIAN_ID";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AssignMaintainerService service;
    private ServiceResponse serviceResponse;
    private EndpointProtector protector;
    private AssignMaintainerRouter router;

    private Person requester;
    private AssignMaintainerData payload;
    private Machine machine;
    private String stringifiedAssignment;

    @Before
    public void setUp() throws Exception {
        service = mock(AssignMaintainerService.class);
        serviceResponse = mock(ServiceResponse.class);
        protector = mock(EndpointProtector.class);

        router = new AssignMaintainerRouter(serviceResponse, protector, service);

        requester = FixtureFactory.generateSupervisor();
        payload = new AssignMaintainerData()
                .setId(MACHINE_ID).setTechnicianId(TECHNICIAN_ID).setRequester(requester);

        machine = FixtureFactory.generateMachine().setId(MACHINE_ID);

        stringifiedAssignment = objectMapper.writeValueAsString(payload);
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        serviceResponse = null;
        protector = null;
        router = null;

        requester = null;
        payload = null;
        machine = null;
        stringifiedAssignment = null;
    }

    @Test
    public void assignMaintainer_withValidData() {
        when(service.assignMaintainer(any())).thenReturn(Optional.ofNullable(machine));

        router.assignMachineMaintainer(MACHINE_ID, stringifiedAssignment, requester);

        verify(service, times(1)).assignMaintainer(payload);
        verify(serviceResponse, times(1)).ok(machine);
    }

    @Test
    public void assignMaintainer_withInvalidData() {
        String[] expectedErrors = new String[]{EMPTY_TECHNICIAN_ID};

        doThrow(new BusinessLogicException(expectedErrors))
                .when(service).assignMaintainer(any());
        router.assignMachineMaintainer(MACHINE_ID, stringifiedAssignment, requester);

        verify(service, times(1)).assignMaintainer(any());
        verify(serviceResponse, times(1)).badRequest(expectedErrors);
    }

    @Test
    public void assignMaintainer_withNotExistingMachineOrTechnician_returnsNotFound() throws Exception {
        when(service.assignMaintainer(payload)).thenReturn(Optional.empty());

        router.assignMachineMaintainer(MACHINE_ID, stringifiedAssignment, requester);

        verify(serviceResponse, times(1)).notFound();
    }

    @Test
    public void assignMaintainer_withEmptyJSON() {
        stringifiedAssignment = "";

        router.assignMachineMaintainer(MACHINE_ID, stringifiedAssignment, requester);

        verify(serviceResponse, never()).ok(any());
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).assignMaintainer(any());
    }

    @Test
    public void assignMaintainer_withInvalidJSONField() {
        stringifiedAssignment = "{\"invalid\":\"1234\"}";

        router.assignMachineMaintainer(MACHINE_ID, stringifiedAssignment, requester);

        verify(serviceResponse, never()).ok(any());
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).assignMaintainer(any());
    }
}