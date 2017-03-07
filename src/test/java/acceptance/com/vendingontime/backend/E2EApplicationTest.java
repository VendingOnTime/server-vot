package acceptance.com.vendingontime.backend;

import com.vendingontime.backend.Application;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import static io.restassured.RestAssured.when;
import static org.hamcrest.core.Is.is;

/**
 * Created by alberto on 7/3/17.
 */
public class E2EApplicationTest {
    @BeforeClass
    public static void setUp() throws Exception {
        Application.main(null);
    }

    @Test
    public void helloTest() throws Exception {
        when()
                .get("http://localhost:8080/api")
        .then()
                .statusCode(200)
                .body(is("Hello World"));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Spark.stop();
    }
}