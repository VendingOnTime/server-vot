package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.routes.LogInRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.vendingontime.backend.models.bodymodels.person.LogInData.BAD_LOGIN;
import static com.vendingontime.backend.routes.LogInRouter.MALFORMED_JSON;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
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

public class LogInRouterTest {
    private static final String EMAIL = "username@test.com";
    private static final String PASSWORD = "PASSWORD";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ServiceResponse serviceResponse;
    private LogInService logInService;
    private LogInRouter logInRouter;
    private LogInData logInData;

    private String stringifiedLogIn;

    @Before
    public void setUp() throws Exception {
        serviceResponse = mock(ServiceResponse.class);
        logInService = mock(LogInService.class);

        logInRouter = new LogInRouter(serviceResponse, logInService);

        logInData = new LogInData();
        logInData.setEmail(EMAIL);
        logInData.setPassword(PASSWORD);

        stringifiedLogIn = objectMapper.writeValueAsString(logInData);
    }

    @After
    public void tearDown() throws Exception {
        serviceResponse = null;
        logInService = null;
        logInRouter = null;
        logInData = null;
        stringifiedLogIn = null;
    }

    @Test
    public void post() {
        when(logInService.authorizeUser(logInData)).thenReturn("generatedToken");

        logInRouter.logInUser(stringifiedLogIn);

        verify(logInService, times(1)).authorizeUser(logInData);
        verify(serviceResponse, times(1)).ok(anyString());
    }

    @Test
    public void post_withEmptyJSON() {
        stringifiedLogIn = "";

        logInRouter.logInUser(stringifiedLogIn);

        verify(logInService, never()).authorizeUser(any());
        verify(serviceResponse, never()).ok(anyString());
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
    }

    @Test
    public void post_withInvalidJSONField() {
        stringifiedLogIn = "{\"id\":\"1234\"}";

        logInRouter.logInUser(stringifiedLogIn);

        verify(logInService, never()).authorizeUser(any());
        verify(serviceResponse, never()).ok(anyString());
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
    }

    @Test
    public void post_withIncorrectData() throws JsonProcessingException {
        logInData.setEmail("nonExisting@mail.com").setPassword("incorrectPwd");
        stringifiedLogIn = objectMapper.writeValueAsString(logInData);

        doThrow(new BusinessLogicException(new String[]{BAD_LOGIN})).when(logInService).authorizeUser(logInData);

        logInRouter.logInUser(stringifiedLogIn);

        verify(serviceResponse, never()).ok(anyString());
        verify(serviceResponse, times(1)).badRequest(new String[]{BAD_LOGIN});
    }
}