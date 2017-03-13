package integration.com.vendingontime.backend.repositories;

import com.vendingontime.backend.initializers.InitDB;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.viewmodels.PersonPayload;
import com.vendingontime.backend.repositories.PersonRepository;
import org.junit.*;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alberto on 13/03/2017.
 */
public class PersonRepositoryTest {
    private static final String DNI = "DNI";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "EMAIL";
    private static final String NAME = "NAME";
    private static final String SURNAME = "SURNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final PersonRole ROLE = PersonRole.SUPERVISOR;

    private PersonRepository repository;
    private Person person;

    @BeforeClass
    public static void mainSetUp() {
        InitDB.generateSchemas();
    }

    @Before
    public void setUp() throws Exception {
        repository = new PersonRepository();

        PersonPayload payload = new PersonPayload();
        payload.setDni(DNI);
        payload.setUsername(USERNAME);
        payload.setEmail(EMAIL);
        payload.setName(NAME);
        payload.setSurnames(SURNAME);
        payload.setPassword(PASSWORD);
        payload.setRole(ROLE);

        person = new Person(payload);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
    }

    @Test
    public void create() throws Exception {
        Person person = repository.create(this.person);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.retrieve(personId);
        assertEquals(person, possiblePerson.get());

        repository.delete(personId);
    }

    @Test
    public void retrieve() throws Exception {
        Person person = repository.create(this.person);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.retrieve(personId);
        assertTrue(possiblePerson.isPresent());

        repository.delete(personId);
    }

    @Test
    public void update() throws Exception {
        Person person = repository.create(this.person);
        String personId = person.getId();

        String newDni = "DNI2";
        person.setDni(newDni);

        Optional<Person> possiblePerson = repository.retrieve(personId);
        assertEquals(newDni, possiblePerson.get().getDni());

        repository.delete(personId);
    }

    @Test
    public void delete() throws Exception {
        Person person = repository.create(this.person);
        String personId = person.getId();

        repository.delete(personId);
    }

}