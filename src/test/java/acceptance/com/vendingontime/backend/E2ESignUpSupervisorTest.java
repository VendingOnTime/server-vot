package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.repositories.PersonRepository;
import org.junit.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.when;
import static org.hamcrest.core.Is.is;

/**
 * Created by alberto on 28/3/17.
 */
public class E2ESignUpSupervisorTest extends E2ETest {

    @Inject
    PersonRepository repository;

    @Test
    public void helloTest() throws Exception {
        when()
                .get(host + "/api")
                .then()
                .statusCode(200)
                .body(is("Hello World"));
    }

}
