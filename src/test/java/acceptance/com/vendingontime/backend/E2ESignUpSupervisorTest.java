package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static java.util.Objects.isNull;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by alberto on 28/3/17.
 */
public class E2ESignUpSupervisorTest extends E2ETest {

    private static final String DNI = "12345678B";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "username@test.com";
    private static final String NAME = "NAME";
    private static final String SURNAME = "SURNAME";
    private static final String PASSWORD = "PASSWORD";

    @Inject
    PersonRepository repository;

    @Test
    public void createSupervisor() throws Exception {

        SignUpData payload = new SignUpData()
                .setDni(DNI)
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setName(NAME)
                .setSurnames(SURNAME)
                .setPassword(PASSWORD);

        given()
                .body(payload)
        .when()
                .post(host + SignUpRouter.V1_SIGN_UP_SUPERVISOR)
        .then()
                .statusCode(HttpResponse.StatusCode.CREATED)
                .body("success", equalTo(true))
                .body("data.dni", equalTo(DNI))
                .body("data.username", equalTo(USERNAME))
                .body("data.email", equalTo(EMAIL))
                .body("data.name", equalTo(NAME))
                .body("data.surnames", equalTo(SURNAME))
                .body("data.password", equalTo(null))
                .body("data.role", equalTo(PersonRole.SUPERVISOR.toString().toLowerCase()))
                .body("error", equalTo(null))
        ;

        Optional<Person> byEmail = repository.findByEmail(EMAIL);
        repository.delete(byEmail.get().getId());
    }

}
