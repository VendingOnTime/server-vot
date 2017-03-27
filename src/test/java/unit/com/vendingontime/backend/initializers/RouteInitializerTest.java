package unit.com.vendingontime.backend.initializers;

import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.initializers.RouteInitializer;
import com.vendingontime.backend.routes.SparkRouter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by alberto on 27/3/17.
 */
public class RouteInitializerTest {
    private RouteInitializer initializer;
    private RESTContext context;

    @Before
    public void setUp() throws Exception {
        context = mock(RESTContext.class);
        SparkRouter testRouter1 = mock(SparkRouter.class);
        SparkRouter testRouter2 = mock(SparkRouter.class);

        HashSet<SparkRouter> routers = new HashSet<>();
        routers.add(testRouter1);
        routers.add(testRouter2);

        initializer = new RouteInitializer(context, routers);
    }

    @After
    public void tearDown() throws Exception {
        initializer = null;
        context = null;
    }

    @Test
    public void setUpCallsConfigureOnRoutes() throws Exception {
        initializer.setUp();

        verify(context, times(2)).addRouter(any());
    }
}