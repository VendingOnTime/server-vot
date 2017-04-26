package unit.com.vendingontime.backend.routes;

import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.GetMachineRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.GetMachineService;
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

public class GetMachineRouterTest {
    private final String MACHINE_ID = "MACHINE_ID";

    private ServiceResponse serviceResponse;
    private GetMachineService service;
    private GetMachineRouter router;

    private Person person;
    private Machine machine;

    @Before
    public void setUp() throws Exception {
        serviceResponse = mock(ServiceResponse.class);
        service = mock(GetMachineService.class);
        EndpointProtector protector = mock(EndpointProtector.class);
        router = new GetMachineRouter(serviceResponse, service, protector);

        person = FixtureFactory.generateSupervisor();
        machine = FixtureFactory.generateMachine().setId(MACHINE_ID);
    }

    @After
    public void tearDown() throws Exception {
        serviceResponse = null;
        service = null;
        router = null;

        person = null;
        machine = null;
    }

    @Test
    public void getMachine_withExistingId_returnsOk() throws Exception {
        when(service.getDataFrom(MACHINE_ID, person)).thenReturn(Optional.of(machine));

        router.getMachine(MACHINE_ID, person);

        verify(serviceResponse, times(1)).ok(machine);
    }

    @Test
    public void getMachine_withExistingId_andUnauthorizedUser_returnsBadRequest() throws Exception {
        Person unauthorizedUser = FixtureFactory.generateSupervisor();

        String[] causes = {INSUFFICIENT_PERMISSIONS};
        doThrow(new BusinessLogicException(causes))
                .when(service).getDataFrom(MACHINE_ID, unauthorizedUser);

        router.getMachine(MACHINE_ID, unauthorizedUser);

        verify(serviceResponse, times(1)).badRequest(causes);
    }

    @Test
    public void getMachine_withNotExistingId_returnsBadRequest() throws Exception {
        final String INVALID_ID = "INVALID_ID";
        when(service.getDataFrom(INVALID_ID, person)).thenReturn(Optional.empty());

        router.getMachine(INVALID_ID, person);

        verify(serviceResponse, times(1)).notFound();
    }

}