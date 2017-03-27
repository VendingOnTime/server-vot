package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.initializers.InitDB;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.bodymodels.LogInData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.LogInRoute;
import com.vendingontime.backend.routes.utils.Response;
import org.junit.*;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.LogInData.BAD_LOGIN;
import static com.vendingontime.backend.routes.SignUpRoute.MALFORMED_JSON;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by miguel on 27/3/17.
 */
public class LogInRouteTest {
    private static final String EMAIL = "username@test.com";
    private static final String PASSWORD = "PASSWORD";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PersonRepository repository;
    private Response response;
    private LogInRoute logIn;
    private LogInData logInData;

    private String stringifiedLogIn;

    @BeforeClass
    public static void mainSetUp() {
        InitDB.generateSchemas();
    }

    @Before
    public void setUp() throws Exception {
        repository = mock(PersonRepository.class);
        response = mock(Response.class);
        logIn = new LogInRoute(repository, response);

        logInData = new LogInData();
        logInData.setEmail(EMAIL);
        logInData.setPassword(PASSWORD);

        stringifiedLogIn = objectMapper.writeValueAsString(logInData);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        response = null;
        logIn = null;
        logInData = null;
        stringifiedLogIn = null;
    }

    @Test
    public void post() {
        Person person = new Person().setEmail(EMAIL).setPassword(PASSWORD);
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(person));

        logIn.post(stringifiedLogIn);

        verify(response, times(1)).ok(anyString());
        verify(repository, times(1)).findByEmail(logInData.getEmail());
    }

    @Test
    public void post_withEmptyJSON() {
        stringifiedLogIn = "";

        logIn.post(stringifiedLogIn);

        verify(response, never()).ok(anyString());
        verify(response, times(1)).badRequest(MALFORMED_JSON);
        verify(repository, never()).findByEmail(anyString());
    }

    @Test
    public void post_withInvalidJSONField() {
        stringifiedLogIn = "{\"id\":\"1234\"}";

        logIn.post(stringifiedLogIn);

        verify(response, never()).ok(anyString());
        verify(response, times(1)).badRequest(MALFORMED_JSON);
        verify(repository, never()).findByEmail(logInData.getEmail());
    }

    @Test
    public void post_withNonExistingEmail() throws JsonProcessingException {
        logInData.setEmail("nonExisting@mail.com");
        stringifiedLogIn = objectMapper.writeValueAsString(logInData);

        logIn.post(stringifiedLogIn);

        verify(response, never()).ok(anyString());
        verify(response, times(1)).badRequest(new String[]{BAD_LOGIN});
        verify(repository, times(1)).findByEmail(logInData.getEmail());
    }

    @Test
    public void post_withIncorrectPassword() throws JsonProcessingException {
        logInData.setPassword("incorrectPwd");
        stringifiedLogIn = objectMapper.writeValueAsString(logInData);

        logIn.post(stringifiedLogIn);

        verify(response, never()).ok(anyString());
        verify(response, times(1)).badRequest(new String[]{BAD_LOGIN});
        verify(repository, times(1)).findByEmail(logInData.getEmail());
    }

    @Test
    public void post_withIncorrectData() throws JsonProcessingException {
        logInData.setEmail("nonExisting@mail.com").setPassword("incorrectPwd");
        stringifiedLogIn = objectMapper.writeValueAsString(logInData);

        logIn.post(stringifiedLogIn);

        verify(response, never()).ok(anyString());
        verify(response, times(1)).badRequest(new String[]{BAD_LOGIN});
        verify(repository, times(1)).findByEmail(logInData.getEmail());
    }
}
