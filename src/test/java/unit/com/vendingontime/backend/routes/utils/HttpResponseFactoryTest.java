package unit.com.vendingontime.backend.routes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.routes.utils.HttpResponseFactory;
import com.vendingontime.backend.routes.utils.RESTResult;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Alberto on 13/03/2017.
 */
public class HttpResponseFactoryTest {
    @Test
    public void json() throws Exception {
        Request req = mock(Request.class);
        Response  res = mock(Response.class);

        String generated = (String) HttpResponseFactory.json().notFound().handle(req, res);

        verify(res, times(1)).type("application/json");
        ObjectMapper om = new ObjectMapper();
        om.readValue(generated, RESTResult.class); // Generates valid JSON
    }
}