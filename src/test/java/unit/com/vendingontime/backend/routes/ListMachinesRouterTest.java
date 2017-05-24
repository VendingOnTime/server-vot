package unit.com.vendingontime.backend.routes;

import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.ListMachinesRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.ListMachinesService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
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
public class ListMachinesRouterTest {
    private ServiceResponse serviceResponse;
    private ListMachinesService service;
    private ListMachinesRouter listMachinesRouter;

    private Person person;
    private List<Machine> machines;

    @Before
    public void setUp() throws Exception {
        serviceResponse = mock(ServiceResponse.class);
        service = mock(ListMachinesService.class);
        EndpointProtector protector = mock(EndpointProtector.class);
        listMachinesRouter = new ListMachinesRouter(serviceResponse, service, protector);

        person = new Person();

        machines = new LinkedList<>();
        machines.add(new Machine());
        machines.add(new Machine());
    }

    @After
    public void tearDown() throws Exception {
        serviceResponse = null;
        service = null;
        listMachinesRouter = null;
        person = null;
        machines = null;
    }

    @Test
    public void listFor_withList_returnsOk() throws Exception {
        when(service.listFor(person)).thenReturn(machines);

        listMachinesRouter.listFor(person);

        verify(serviceResponse, times(1)).ok(machines);
    }

    @Test
    public void listFor_withException_returnsBadRequest() throws Exception {
        String[] causes = {INSUFFICIENT_PERMISSIONS};
        doThrow(new BusinessLogicException(causes))
                .when(service).listFor(person);

        listMachinesRouter.listFor(person);

        verify(serviceResponse, times(1)).badRequest(causes);
    }
}