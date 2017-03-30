package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import org.junit.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.core.Is.is;

/**
 * Created by alberto on 7/3/17.
 */
public class E2EExampleTest extends E2ETest {

    @Test
    public void helloTest() throws Exception {
        when()
                .get(host + "/api")
        .then()
                .statusCode(200)
                .body(is("Hello World"));
    }
}