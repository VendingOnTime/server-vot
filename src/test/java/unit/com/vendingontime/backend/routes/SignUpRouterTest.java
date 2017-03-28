package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.utils.Response;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.vendingontime.backend.models.PersonCollisionException.DNI_EXISTS;
import static com.vendingontime.backend.models.PersonCollisionException.EMAIL_EXISTS;
import static com.vendingontime.backend.models.PersonCollisionException.USERNAME_EXISTS;
import static com.vendingontime.backend.models.bodymodels.SignUpData.INVALID_DNI;
import static com.vendingontime.backend.routes.SignUpRouter.MALFORMED_JSON;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by alberto on 28/3/17.
 */
public class SignUpRouterTest {
    private static final String DNI = "12345678B";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "username@test.com";
    private static final String NAME = "NAME";
    private static final String SURNAME = "SURNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final PersonRole ROLE = PersonRole.SUPERVISOR;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SignUpService service;
    private Response response;
    private SignUpRouter signUp;
    private SignUpData payload;

    private Person person;
    private String stringifiedPerson;

    @Before
    public void setUp() throws Exception {
        service = mock(SignUpService.class);
        response = mock(Response.class);
        signUp = new SignUpRouter(service, response);

        payload = new SignUpData()
                .setDni(DNI)
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setName(NAME)
                .setSurnames(SURNAME)
                .setPassword(PASSWORD)
                .setRole(ROLE);

        person = new Person(payload);

        stringifiedPerson = objectMapper.writeValueAsString(payload);
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        response = null;
        signUp = null;
        payload = null;
        stringifiedPerson = null;
    }

    @Test
    public void post() {
        when(service.createUser(payload)).thenReturn(person);
        signUp.post(stringifiedPerson);

        verify(response, times(1)).created(person);
        verify(service, times(1)).createUser(payload);
    }

    @Test
    public void post_withInvalidData() {
        doThrow(new BusinessLogicException(new String[]{INVALID_DNI}))
                .when(service).createUser(payload);
        signUp.post(stringifiedPerson);

        verify(service, times(1)).createUser(payload);
        verify(response, times(1)).badRequest(any());
    }

    @Test
    public void post_withEmptyJSON() {
        stringifiedPerson = "";

        signUp.post(stringifiedPerson);

        verify(response, never()).created(person);
        verify(response, times(1)).badRequest(any());
        verify(service, never()).createUser(payload);
    }

    @Test
    public void post_withInvalidJSONField() {
        stringifiedPerson = "{\"id\":\"1234\"}";

        signUp.post(stringifiedPerson);

        verify(response, never()).created(person);
        verify(response, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).createUser(payload);
    }
}