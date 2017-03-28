package integration.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.RESTResult;
import integration.com.vendingontime.backend.repositories.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by alberto on 28/3/17.
 */
public class SignUpRouterTest extends IntegrationTest {

    @Inject
    SignUpRouter router;

    @Inject
    PersonRepository repository;

    private ObjectMapper mapper;
    private SignUpData supervisor;
    private String stringifiedSupervisor;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        supervisor = new SignUpData()
                .setRole(PersonRole.SUPERVISOR)
                .setEmail("user@example.com")
                .setUsername("user")
                .setPassword("12345")
                .setName("name")
                .setSurnames("surnames");
        stringifiedSupervisor = mapper.writeValueAsString(supervisor);
    }

    @After
    public void tearDown() throws Exception {
        supervisor = null;
        stringifiedSupervisor = null;
    }

    @Test
    public void post() throws Exception {
        AppRoute post = router.post(stringifiedSupervisor);
        String result = (String) post.handle(mock(Request.class), mock(Response.class));

        RESTResult restResult = mapper.readValue(result, RESTResult.class);

        assertTrue(restResult.getSuccess());

        Optional<Person> byEmail = repository.findByEmail(supervisor.getEmail());
        assertTrue(byEmail.isPresent());

        repository.delete(byEmail.get().getId());
    }

}