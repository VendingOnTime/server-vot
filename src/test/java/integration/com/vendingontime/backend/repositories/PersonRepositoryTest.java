package integration.com.vendingontime.backend.repositories;

import com.vendingontime.backend.initializers.InitDB;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SubmitData;
import com.vendingontime.backend.repositories.PersonRepository;
import org.junit.*;

import java.util.Optional;

import static com.vendingontime.backend.models.PersonCollisionException.DNI_EXISTS;
import static com.vendingontime.backend.models.PersonCollisionException.EMAIL_EXISTS;
import static com.vendingontime.backend.models.PersonCollisionException.USERNAME_EXISTS;
import static junit.framework.TestCase.*;

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
    private Person personOne;
    private Person personTwo;

    @BeforeClass
    public static void mainSetUp() {
        InitDB.generateSchemas();
    }

    @Before
    public void setUp() throws Exception {
        repository = new PersonRepository();

        SubmitData payload = new SubmitData();
        payload.setDni(DNI);
        payload.setUsername(USERNAME);
        payload.setEmail(EMAIL);
        payload.setName(NAME);
        payload.setSurnames(SURNAME);
        payload.setPassword(PASSWORD);
        payload.setRole(ROLE);

        personOne = new Person(payload);
        personTwo = new Person(payload);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        personOne = null;
        personTwo = null;
    }

    @Test
    public void create() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findById(personId);
        assertEquals(person, possiblePerson.get());

        repository.delete(personId);
    }

    @Test
    public void create_withEmailCollision() throws Exception {
        Person person = repository.create(personOne);
        String personId = person.getId();

        String EMAIL2 = EMAIL;
        String USERNAME2 = "USERNAME2";
        String DNI2 = "DNI2";
        personTwo.setDni(DNI2);
        personTwo.setUsername(USERNAME2);
        personTwo.setEmail(EMAIL2);

        try {
            repository.create(personTwo);
            fail();
        } catch (PersonCollisionException e) {
            assertEquals(EMAIL_EXISTS, e.getMessage());
        }

        repository.delete(personId);
    }

    @Test
    public void create_withUsernameCollision() throws Exception {
        Person person = repository.create(personOne);
        String personId = person.getId();

        String EMAIL2 = "EMAIL2";
        String USERNAME2 = USERNAME;
        String DNI2 = "DNI2";
        personTwo.setDni(DNI2);
        personTwo.setUsername(USERNAME2);
        personTwo.setEmail(EMAIL2);

        try {
            repository.create(personTwo);
            fail();
        } catch (PersonCollisionException e) {
            assertEquals(USERNAME_EXISTS, e.getMessage());
        }

        repository.delete(personId);
    }

    @Test
    public void create_withDniCollision() throws Exception {
        Person person = repository.create(personOne);
        String personId = person.getId();

        String EMAIL2 = "EMAIL2";
        String USERNAME2 = "USERNAME2";
        String DNI2 = DNI;
        personTwo.setDni(DNI2);
        personTwo.setUsername(USERNAME2);
        personTwo.setEmail(EMAIL2);

        try {
            repository.create(personTwo);
            fail();
        } catch (PersonCollisionException e) {
            assertEquals(DNI_EXISTS, e.getMessage());
        }

        repository.delete(personId);
    }

    @Test
    public void findById() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findById(personId);
        assertTrue(possiblePerson.isPresent());
        assertEquals(personId, possiblePerson.get().getId());

        repository.delete(personId);
    }

    @Test
    public void findById_null_returnsEmpty() throws Exception {
        Optional<Person> possiblePerson = repository.findById(null);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void findByEmail() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findByEmail(EMAIL);
        assertTrue(possiblePerson.isPresent());
        assertEquals(EMAIL, possiblePerson.get().getEmail());

        repository.delete(personId);
    }

    @Test
    public void findByEmail_null_returnsEmpty() throws Exception {
        Optional<Person> possiblePerson = repository.findByEmail(null);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void findByUsername() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findByUsername(USERNAME);
        assertTrue(possiblePerson.isPresent());
        assertEquals(USERNAME, possiblePerson.get().getUsername());

        repository.delete(personId);
    }

    @Test
    public void findByUsername_null_returnsEmpty() throws Exception {
        Optional<Person> possiblePerson = repository.findByUsername(null);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void findByDni() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findByDni(DNI);
        assertTrue(possiblePerson.isPresent());
        assertEquals(DNI, possiblePerson.get().getDni());

        repository.delete(personId);
    }

    @Test
    public void findByDni_null_returnsEmpty() throws Exception {
        Optional<Person> possiblePerson = repository.findByDni(null);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void update() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        String newDni = "DNI2";
        person.setDni(newDni);

        Optional<Person> possiblePerson = repository.findById(personId);
        assertEquals(newDni, possiblePerson.get().getDni());

        repository.delete(personId);
    }

    @Test
    public void delete() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        repository.delete(personId);

        Optional<Person> possiblePerson = repository.findById(personId);
        assertFalse(possiblePerson.isPresent());
    }

}