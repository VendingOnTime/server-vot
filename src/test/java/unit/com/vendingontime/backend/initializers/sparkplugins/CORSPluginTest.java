package unit.com.vendingontime.backend.initializers.sparkplugins;

import com.vendingontime.backend.initializers.sparkplugins.CORSPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
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
public class CORSPluginTest {
    private Request request;
    private Response response;
    private CORSPlugin plugin;

    @Before
    public void setUp() throws Exception {
        request = mock(Request.class);
        response = mock(Response.class);
        plugin = new CORSPlugin();
    }

    @After
    public void tearDown() throws Exception {
        request = null;
        response = null;
        plugin = null;
    }

    @Test
    public void options_withCORS() throws Exception {
        String REQUEST_HEADER = "x-authorization";
        String REQUEST_METHOD = "GET";

        when(request.headers("Access-Control-Request-Headers")).thenReturn(REQUEST_HEADER);
        when(request.headers("Access-Control-Request-Method")).thenReturn(REQUEST_METHOD);

        String responseBody = plugin.options(request, this.response);
        assertEquals("OK", responseBody);
        verify(response, times(1)).header("Access-Control-Allow-Headers", REQUEST_HEADER);
        verify(response, times(1)).header("Access-Control-Allow-Methods", REQUEST_METHOD);
    }

    @Test
    public void options_withoutCORS() throws Exception {
        String responseBody = plugin.options(request, this.response);
        assertEquals("OK", responseBody);
        verify(response, times(0)).header(anyString(), any());
    }

    @Test
    public void before() throws Exception {
        plugin.before(request, response);

        verify(response, times(1)).header("Access-Control-Allow-Origin", "*");
        verify(response, times(1)).header("Access-Control-Request-Method", "GET, POST, PUT, DELETE");
        verify(response, times(1)).header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    }

}