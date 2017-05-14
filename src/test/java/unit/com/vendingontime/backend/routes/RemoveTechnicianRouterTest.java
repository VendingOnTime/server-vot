package unit.com.vendingontime.backend.routes;

import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.RemoveTechnicianRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.RemoveTechnicianService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
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
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class RemoveTechnicianRouterTest {
    private static final String TECHNICIAN_ID = "TECHNICIAN_ID";

    private RemoveTechnicianService service;
    private ServiceResponse serviceResponse;
    private RemoveTechnicianRouter router;
    private Person technician;
    private Person requester;

    @Before
    public void setUp() throws Exception {
        service = mock(RemoveTechnicianService.class);
        serviceResponse = mock(ServiceResponse.class);
        EndpointProtector protector = mock(EndpointProtector.class);

        router = new RemoveTechnicianRouter(serviceResponse, protector, service);

        technician = FixtureFactory.generateTechnician();
        requester = FixtureFactory.generateSupervisorWithCompany();
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        serviceResponse = null;
        router = null;
        technician = null;
        requester = null;
    }

    @Test
    public void removeTechnician_withValidData() {
        when(service.remove(any())).thenReturn(Optional.ofNullable(technician));

        router.remove(TECHNICIAN_ID, requester);

        verify(service, times(1)).remove(any());
        verify(serviceResponse, times(1)).ok(technician);
    }

    @Test
    public void removeTechnician_withUnauthorizedUser() {
        String[] expectedErrors = new String[]{ INSUFFICIENT_PERMISSIONS };

        doThrow(new BusinessLogicException(expectedErrors))
                .when(service).remove(any());
        router.remove(TECHNICIAN_ID, requester);

        verify(service, times(1)).remove(any());
        verify(serviceResponse, times(1)).badRequest(expectedErrors);
    }

    @Test
    public void removeTechnician_withNotExistingMachine_returnsNotFound() throws Exception {
        when(service.remove(any())).thenReturn(Optional.empty());

        router.remove(TECHNICIAN_ID, requester);

        verify(serviceResponse, times(1)).notFound();
    }
}