package unit.com.vendingontime.backend.routes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.routes.utils.JSONTransformer;
import com.vendingontime.backend.routes.utils.ResponseGenerator;
import com.vendingontime.backend.routes.utils.ResponseWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Alberto on 13/03/2017.
 */
public class ResponseGeneratorTest {
    private static final String CONTENT_TYPE = "CONTENT_TYPE";

    private ResponseGenerator generator;
    private Request req;
    private Response res;

    @Before
    public void setUp() throws Exception {
        generator = new ResponseGenerator(CONTENT_TYPE, new JSONTransformer());
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
        ResponseWrapper wrapper = om.readValue(generated, ResponseWrapper.class);

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
        ResponseWrapper wrapper = om.readValue(generated, ResponseWrapper.class);

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
        ResponseWrapper wrapper = om.readValue(generated, ResponseWrapper.class);

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
        ResponseWrapper wrapper = om.readValue(generated, ResponseWrapper.class);

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
        ResponseWrapper wrapper = om.readValue(generated, ResponseWrapper.class);

        assertFalse(wrapper.getSuccess());
        assertNull(wrapper.getData());
        assertEquals(NOT_FOUND, wrapper.getError());
    }

}