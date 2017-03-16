package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.initializers.InitDB;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.SignUpRoute;
import com.vendingontime.backend.routes.utils.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by miguel on 13/3/17.
 */
public class SignUpRouteTest {
    private static final String ID = "ID";
    private static final String DNI = "DNI";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "EMAIL";
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
    public void createUser() {
        signUp.post(stringifiedPerson);

        verify(response, times(1)).created(person);
        verify(repository, times(1)).create(person);
    }
}