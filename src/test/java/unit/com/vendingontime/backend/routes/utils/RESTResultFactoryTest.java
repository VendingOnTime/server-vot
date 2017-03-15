package unit.com.vendingontime.backend.routes.utils;

import com.vendingontime.backend.routes.utils.RESTResult;
import com.vendingontime.backend.routes.utils.RESTResultFactory;
import com.vendingontime.backend.routes.utils.ResultFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alberto on 15/3/17.
 */
public class RESTResultFactoryTest {

    private ResultFactory resultFactory;

    @Before
    public void setUp() throws Exception {
        resultFactory = new RESTResultFactory();
    }

    @After
    public void tearDown() throws Exception {
        resultFactory = null;
    }

    @Test
    public void ok() throws Exception {
        String data = "OK";
        RESTResult ok = (RESTResult) resultFactory.ok(data);
        assertTrue(ok.getSuccess());
        assertEquals(data, ok.getData());
        assertNull(ok.getError());
    }

    @Test
    public void error() throws Exception {
        String cause = "ERROR";
        RESTResult error = (RESTResult) resultFactory.error(cause);
        assertFalse(error.getSuccess());
        assertNull(error.getData());
        assertEquals(cause, error.getError());
    }

}