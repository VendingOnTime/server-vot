package acceptance.com.vendingontime.backend.testutils;

import com.vendingontime.backend.config.variables.MemoryServerConfig;
import com.vendingontime.backend.config.variables.ServerVariable;
import integration.com.vendingontime.backend.repositories.testutils.IntegrationTest;

/**
 * Created by alberto on 28/3/17.
 */
public abstract class E2ETest extends IntegrationTest {
    protected static final String host = "http://localhost:"
            + new MemoryServerConfig().getString(ServerVariable.PORT);
}
