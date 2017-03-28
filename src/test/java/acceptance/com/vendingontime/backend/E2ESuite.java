package acceptance.com.vendingontime.backend;

import com.vendingontime.backend.Application;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import spark.Spark;


/**
 * Created by alberto on 28/3/17.
 */

// TODO Add extra E2E tests here to ensure that they run with a server running

@RunWith(Suite.class)
@Suite.SuiteClasses({
        E2EExampleTest.class,
        E2ESignUpSupervisorTest.class
})
public class E2ESuite {

    @BeforeClass
    public static void setUp() throws Exception {
        Application.main(null);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Spark.stop();
    }
}
