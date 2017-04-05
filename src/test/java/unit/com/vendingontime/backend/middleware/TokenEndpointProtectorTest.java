package unit.com.vendingontime.backend.middleware;

import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.middleware.TokenEndpointProtector;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.services.utils.TokenGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Request;

import java.util.Optional;

import static com.vendingontime.backend.middleware.TokenEndpointProtector.LOGGED_IN_PERSON;
import static com.vendingontime.backend.middleware.TokenEndpointProtector.AUTHORIZATION;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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

public class TokenEndpointProtectorTest {

    private static final String TOKEN_STRATEGY_TYPE = "TST";
    private static final String TOKEN = "token";

    private RESTContext context;
    private TokenGenerator tokenGenerator;
    private TokenEndpointProtector endpointProtector;

    private Person person;

    @Before
    public void setUp() throws Exception {
        context = mock(RESTContext.class);
        tokenGenerator = mock(TokenGenerator.class);
        endpointProtector = new TokenEndpointProtector(TOKEN_STRATEGY_TYPE, context, tokenGenerator);

        person = new Person();

        when(tokenGenerator.recoverFrom(TOKEN)).thenReturn(Optional.of(person));
    }

    @After
    public void tearDown() throws Exception {
        context = null;
        tokenGenerator = null;
        endpointProtector = null;
        person = null;
    }

    @Test
    public void protect() throws Exception {
        endpointProtector.protect("uri");
        verify(context, times(1)).addMiddleware(endpointProtector);
    }

    @Test
    public void fillRequestWithPersonIfAuthorized_withTokenAndPerson_returnsTrue() throws Exception {
        Request req = mock(Request.class);
        when(req.headers(AUTHORIZATION)).thenReturn(TOKEN_STRATEGY_TYPE + " " + TOKEN);

        boolean loggedIn = endpointProtector.fillRequestWithPersonIfAuthorized(req);
        assertTrue(loggedIn);
        verify(req, times(1)).attribute(LOGGED_IN_PERSON, person);
    }

    @Test
    public void fillRequestWithPersonIfAuthorized_withNoToken_returnsFalse() throws Exception {
        Request req = mock(Request.class);

        boolean loggedIn = endpointProtector.fillRequestWithPersonIfAuthorized(req);
        assertFalse(loggedIn);
        verify(req, never()).attribute(anyString(), any());
    }

    @Test
    public void fillRequestWithPersonIfAuthorized_withWrongHeaderFormat_returnsFalse() throws Exception {
        Request req = mock(Request.class);
        when(req.headers(AUTHORIZATION)).thenReturn(TOKEN);

        boolean loggedIn = endpointProtector.fillRequestWithPersonIfAuthorized(req);
        assertFalse(loggedIn);
        verify(req, never()).attribute(anyString(), any());
    }

    @Test
    public void fillRequestWithPersonIfAuthorized_withInvalidTokenStrategy_returnsFalse() throws Exception {
        Request req = mock(Request.class);
        when(req.headers(AUTHORIZATION)).thenReturn("ITS " + TOKEN);

        boolean loggedIn = endpointProtector.fillRequestWithPersonIfAuthorized(req);
        assertFalse(loggedIn);
        verify(req, never()).attribute(anyString(), any());
    }

    @Test
    public void fillRequestWithPersonIfAuthorized_withInvalidToken_returnsFalse() throws Exception {
        Request req = mock(Request.class);
        when(req.headers(AUTHORIZATION)).thenReturn(TOKEN_STRATEGY_TYPE + " invalidToken");

        boolean loggedIn = endpointProtector.fillRequestWithPersonIfAuthorized(req);
        assertFalse(loggedIn);
        verify(req, never()).attribute(anyString(), any());
    }
}