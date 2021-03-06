package unit.com.vendingontime.backend.routes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.routes.utils.JSONTransformer;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.routes.utils.RESTResult;
import com.vendingontime.backend.routes.utils.RESTResultFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
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
public class HttpResponseTest {
    private static final String CONTENT_TYPE = "CONTENT_TYPE";

    private HttpResponse generator;
    private Request req;
    private Response res;

    @Before
    public void setUp() throws Exception {
        generator = new HttpResponse(CONTENT_TYPE, new JSONTransformer(), new RESTResultFactory());
        req = mock(Request.class);
        res = mock(Response.class);
    }

    @After
    public void tearDown() throws Exception {
        generator = null;
        req = null;
        res = null;
    }

    @Test
    public void ok() throws Exception {
        String OK = "OK";
        int STATUS = 200;
        String generated = (String) generator.ok(OK).handle(req, res);

        verify(res, times(1)).status(STATUS);
        verify(res, times(1)).type(CONTENT_TYPE);

        ObjectMapper om = new ObjectMapper();
        RESTResult wrapper = om.readValue(generated, RESTResult.class);

        assertTrue(wrapper.getSuccess());
        assertEquals(OK, wrapper.getData());
        assertNull(wrapper.getError());
    }

    @Test
    public void created() throws Exception {
        String CREATED = "CREATED";
        int STATUS = 201;
        String generated = (String) generator.created(CREATED).handle(req, res);

        verify(res, times(1)).status(STATUS);
        verify(res, times(1)).type(CONTENT_TYPE);

        ObjectMapper om = new ObjectMapper();
        RESTResult wrapper = om.readValue(generated, RESTResult.class);

        assertTrue(wrapper.getSuccess());
        assertEquals(CREATED, wrapper.getData());
        assertNull(wrapper.getError());
    }

    @Test
    public void badRequest() throws Exception {
        String BAD_REQUEST = "BAD_REQUEST";
        int STATUS = 400;
        String generated = (String) generator.badRequest(BAD_REQUEST).handle(req, res);

        verify(res, times(1)).status(STATUS);
        verify(res, times(1)).type(CONTENT_TYPE);

        ObjectMapper om = new ObjectMapper();
        RESTResult wrapper = om.readValue(generated, RESTResult.class);

        assertFalse(wrapper.getSuccess());
        assertNull(wrapper.getData());
        assertEquals(BAD_REQUEST, wrapper.getError());
    }

    @Test
    public void unauthorized() throws Exception {
        String UNAUTHORIZED = "UNAUTHORIZED";
        int STATUS = 401;
        String generated = (String) generator.unauthorized().handle(req, res);

        verify(res, times(1)).status(STATUS);
        verify(res, times(1)).type(CONTENT_TYPE);

        ObjectMapper om = new ObjectMapper();
        RESTResult wrapper = om.readValue(generated, RESTResult.class);

        assertFalse(wrapper.getSuccess());
        assertNull(wrapper.getData());
        assertEquals(UNAUTHORIZED, wrapper.getError());
    }

    @Test
    public void notFound() throws Exception {
        String NOT_FOUND = "NOT_FOUND";
        int STATUS = 404;
        String generated = (String) generator.notFound().handle(req, res);

        verify(res, times(1)).status(STATUS);
        verify(res, times(1)).type(CONTENT_TYPE);

        ObjectMapper om = new ObjectMapper();
        RESTResult wrapper = om.readValue(generated, RESTResult.class);

        assertFalse(wrapper.getSuccess());
        assertNull(wrapper.getData());
        assertEquals(NOT_FOUND, wrapper.getError());
    }

}