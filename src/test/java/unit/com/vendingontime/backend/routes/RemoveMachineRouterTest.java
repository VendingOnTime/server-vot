package unit.com.vendingontime.backend.routes;

import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.RemoveMachineRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.RemoveMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
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

public class RemoveMachineRouterTest {
    private static final String MACHINE_ID = "MACHINE_ID";

    private RemoveMachineService service;
    private ServiceResponse serviceResponse;
    private RemoveMachineRouter router;
    private Machine machine;
    private Person requester;

    @Before
    public void setUp() throws Exception {
        service = mock(RemoveMachineService.class);
        serviceResponse = mock(ServiceResponse.class);
        EndpointProtector protector = mock(EndpointProtector.class);

        router = new RemoveMachineRouter(serviceResponse, service, protector);

        machine = new Machine(FixtureFactory.generateAddMachineData()).setId(MACHINE_ID);
        requester = FixtureFactory.generateSupervisorWithCompany();
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        serviceResponse = null;
        router = null;
        machine = null;
        requester = null;
    }

    @Test
    public void removeMachine_withValidData() {
        when(service.removeMachine(any())).thenReturn(Optional.ofNullable(machine));

        router.removeMachine(MACHINE_ID, requester);

        verify(service, times(1))
                .removeMachine(any());
        verify(serviceResponse, times(1)).ok(machine);
    }

    @Test
    public void removeMachine_withUnauthorizedUser() {
        String[] expectedErrors = new String[]{ INSUFFICIENT_PERMISSIONS };

        doThrow(new BusinessLogicException(expectedErrors))
                .when(service).removeMachine(any());
        router.removeMachine(MACHINE_ID, requester);

        verify(service, times(1)).removeMachine(any());
        verify(serviceResponse, times(1)).badRequest(expectedErrors);
    }

    @Test
    public void removeMachine_withNotExistingMachine_returnsNotFound() throws Exception {
        when(service.removeMachine(any())).thenReturn(Optional.empty());

        router.removeMachine(MACHINE_ID, requester);

        verify(serviceResponse, times(1)).notFound();
    }


}