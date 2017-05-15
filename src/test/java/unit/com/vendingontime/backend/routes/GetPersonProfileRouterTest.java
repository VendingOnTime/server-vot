package unit.com.vendingontime.backend.routes;

import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.GetPersonProfileRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.GetPersonService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
public class GetPersonProfileRouterTest {
    private final String PERSON_ID = "PERSON_ID";

    private ServiceResponse serviceResponse;
    private GetPersonService service;
    private GetPersonProfileRouter router;

    private Person requester;
    private Person person;

    @Before
    public void setUp() throws Exception {
        serviceResponse = mock(ServiceResponse.class);
        service = mock(GetPersonService.class);
        EndpointProtector protector = mock(EndpointProtector.class);
        router = new GetPersonProfileRouter(serviceResponse, service, protector);

        requester = FixtureFactory.generateSupervisor();
        person = FixtureFactory.generateTechnician().setId(PERSON_ID);
    }

    @After
    public void tearDown() throws Exception {
        serviceResponse = null;
        service = null;
        router = null;

        requester = null;
        person = null;
    }

    @Test
    public void getTechnician_withExistingId_returnsOk() throws Exception {
        when(service.getBy(any())).thenReturn(Optional.of(person));

        router.getWith(PERSON_ID, requester);

        verify(serviceResponse, times(1)).ok(person);
    }

    @Test
    public void getTechnician_withExistingId_andUnauthorizedUser_returnsBadRequest() throws Exception {
        Person unauthorizedUser = FixtureFactory.generateSupervisor();

        String[] causes = {INSUFFICIENT_PERMISSIONS};
        doThrow(new BusinessLogicException(causes)).when(service).getBy(any());

        router.getWith(PERSON_ID, unauthorizedUser);

        verify(serviceResponse, times(1)).badRequest(causes);
    }

    @Test
    public void getTechnician_withNotExistingId_returnsBadRequest() throws Exception {
        final String INVALID_ID = "INVALID_ID";
        when(service.getBy(any())).thenReturn(Optional.empty());

        router.getWith(INVALID_ID, requester);

        verify(serviceResponse, times(1)).notFound();
    }
}