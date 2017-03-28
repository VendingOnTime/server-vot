package integration.com.vendingontime.backend.repositories.testutils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vendingontime.backend.config.inject.ConfigModule;
import org.junit.Before;

/**
 * Created by alberto on 28/3/17.
 */
public abstract class IntegrationTest {
    private static final Injector injector = Guice.createInjector(new ConfigModule());

    @Before
    public void injectContext() throws Exception {
        injector.injectMembers(this);
    }
}
