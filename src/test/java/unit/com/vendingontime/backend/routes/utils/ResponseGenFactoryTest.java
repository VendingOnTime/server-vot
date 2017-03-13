package unit.com.vendingontime.backend.routes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.routes.utils.ResponseGenFactory;
import com.vendingontime.backend.routes.utils.ResponseWrapper;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Alberto on 13/03/2017.
 */
public class ResponseGenFactoryTest {
    @Test
    public void json() throws Exception {
        Request req = mock(Request.class);
        Response  res = mock(Response.class);

        String generated = (String) ResponseGenFactory.json().notFound().handle(req, res);

        verify(res, times(1)).type("application/json");
        ObjectMapper om = new ObjectMapper();
        om.readValue(generated, ResponseWrapper.class); // Generates valid JSON
    }
}