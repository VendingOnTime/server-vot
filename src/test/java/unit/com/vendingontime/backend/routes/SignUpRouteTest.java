package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.initializers.InitDB;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;

import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.SignUpRoute;
import com.vendingontime.backend.routes.utils.Response;

import org.junit.*;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;
import static com.vendingontime.backend.models.PersonCollisionException.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by miguel on 13/3/17.
 */
public class SignUpRouteTest {
    private static final String DNI = "11111111A";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "user@example.com";
    private static final String NAME = "NAME";
    private static final String SURNAME = "SURNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final PersonRole ROLE = PersonRole.SUPERVISOR;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PersonRepository repository;
    private Response response;
    private SignUpRoute signUp;
    private SignUpData payload;
    private Person person;
    private String stringifiedPerson;

    @BeforeClass
    public static void mainSetUp() {
        InitDB.generateSchemas();
    }

    @Before
    public void setUp() throws Exception {
        repository = mock(PersonRepository.class);
        response = mock(Response.class);
        signUp = new SignUpRoute(repository, response);

        payload = new SignUpData();
        payload.setDni(DNI);
        payload.setUsername(USERNAME);
        payload.setEmail(EMAIL);
        payload.setName(NAME);
        payload.setSurnames(SURNAME);
        payload.setPassword(PASSWORD);
        payload.setRole(ROLE);

        person = new Person(payload);

        stringifiedPerson = objectMapper.writeValueAsString(payload);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        response = null;
        signUp = null;
        payload = null;
        stringifiedPerson = null;
    }

    @Test
    public void post() {
        signUp.post(stringifiedPerson);

        verify(response, times(1)).created(person);
        verify(repository, times(1)).create(person);
    }

    @Test
    public void post_withEmptyJSON() {
        stringifiedPerson = "";

        signUp.post(stringifiedPerson);

        verify(response, never()).created(person);
        verify(response, times(1)).badRequest(any());
        verify(repository, never()).create(person);
    }

    @Test
    public void post_withInvalidJSONField() {
        stringifiedPerson = "{\"id\":\"1234\"}";

        signUp.post(stringifiedPerson);

        verify(response, never()).created(person);
        verify(response, times(1)).badRequest(any());
        verify(repository, never()).create(person);
    }

    //TODO incorrect data tests?

    @Test
    @Ignore
    public void post_withExistingUniqueData() {
        List<Cause> causes = new LinkedList<>();
        causes.add(Cause.DNI);
        causes.add(Cause.EMAIL);
        causes.add(Cause.USERNAME);

        String[] stringCauses = new String[] {DNI_EXISTS, EMAIL_EXISTS, USERNAME_EXISTS};

        signUp.post(stringifiedPerson);

        doThrow(new PersonCollisionException(causes.toArray(new Cause[causes.size()]))).when(repository).create(person);

        signUp.post(stringifiedPerson);

        verify(response, times(1)).created(person);
        verify(response, times(1)).badRequest(stringCauses);
        verify(repository, times(2)).create(person);
    }
}