package unit.com.vendingontime.backend.config;

import com.vendingontime.backend.config.Environment;
import com.vendingontime.backend.config.MemoryServerConfig;
import com.vendingontime.backend.config.ServerVariable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alberto on 27/3/17.
 */
public class MemoryServerConfigTest {
    private MemoryServerConfig config;

    @Before
    public void setUp() throws Exception {
        config = new MemoryServerConfig();
    }

    @After
    public void tearDown() throws Exception {
        config = null;
    }

    @Test
    public void getString() throws Exception {
        assertEquals(Environment.DEVELOPMENT.toString(), config.getString(ServerVariable.ENV));
    }

    @Test
    public void getInt() throws Exception {
        int PORT = 80;
        config.setVariable(ServerVariable.PORT, String.valueOf(PORT));
        assertEquals(PORT, config.getInt(ServerVariable.PORT));
    }

}