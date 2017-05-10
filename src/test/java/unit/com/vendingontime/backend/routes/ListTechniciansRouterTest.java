package unit.com.vendingontime.backend.routes;

import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.routes.ListTechniciansRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.ListTechniciansService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

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

public class ListTechniciansRouterTest {
    private ServiceResponse serviceResponse;
    private ListTechniciansService service;
    private ListTechniciansRouter listTechniciansRouter;

    private Person person;
    private List<Person> technicians;

    @Before
    public void setUp() throws Exception {
        serviceResponse = mock(ServiceResponse.class);
        EndpointProtector protector = mock(EndpointProtector.class);
        service = mock(ListTechniciansService.class);
        listTechniciansRouter = new ListTechniciansRouter(serviceResponse, protector, service);

        person = new Person();

        technicians = new LinkedList<>();
        technicians.add(new Person().setRole(PersonRole.TECHNICIAN));
        technicians.add(new Person().setRole(PersonRole.TECHNICIAN));
    }

    @After
    public void tearDown() throws Exception {
        serviceResponse = null;
        service = null;
        listTechniciansRouter = null;
        person = null;
        technicians = null;
    }

    @Test
    public void listFor_withList_returnsOk() throws Exception {
        when(service.listFor(person)).thenReturn(technicians);

        listTechniciansRouter.listFor(person);

        verify(serviceResponse, times(1)).ok(technicians);
    }

    @Test
    public void listFor_withException_returnsBadRequest() throws Exception {
        String[] causes = {INSUFFICIENT_PERMISSIONS};
        doThrow(new BusinessLogicException(causes))
                .when(service).listFor(person);

        listTechniciansRouter.listFor(person);

        verify(serviceResponse, times(1)).badRequest(causes);
    }
}