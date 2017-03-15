package integration.com.vendingontime.backend.repositories;

import com.vendingontime.backend.initializers.InitDB;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.viewmodels.PersonPayload;
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

    private static final String DNI2 = "DNI2";
    private static final String USERNAME2 = "USERNAME2";
    private static final String EMAIL2 = "EMAIL2";


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

        PersonPayload payload = new PersonPayload();
        payload.setDni(DNI);
        payload.setUsername(USERNAME);
        payload.setEmail(EMAIL);
        payload.setName(NAME);
        payload.setSurnames(SURNAME);
        payload.setPassword(PASSWORD);
        payload.setRole(ROLE);

        personOne = new Person(payload);
        personTwo = new Person(payload);

        personTwo.setDni(DNI2);
        personTwo.setEmail(EMAIL2);
        personTwo.setUsername(USERNAME2);
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
    public void create_twoWithoutCollisions() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        Optional<Person> possiblePersonOne = repository.findById(personOneId);
        assertEquals(personOne, possiblePersonOne.get());
        Optional<Person> possiblePersonTwo = repository.findById(personTwoId);
        assertEquals(personTwo, possiblePersonTwo.get());

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void create_withEmailCollision() throws Exception {
        Person person = repository.create(personOne);
        String personId = person.getId();

        personTwo.setEmail(EMAIL);

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

        personTwo.setUsername(USERNAME);

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

        personTwo.setDni(DNI);

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

        person.setDni(DNI2);
        repository.update(person);

        Optional<Person> possiblePerson = repository.findById(personId);
        assertEquals(DNI2, possiblePerson.get().getDni());

        repository.delete(personId);
    }

    @Test(expected = NullPointerException.class)
    public void update_null_returnsEmpty() throws Exception {
        repository.update(null);
    }

    @Test
    public void update_withEmailCollision() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        personOne.setEmail(EMAIL2);
        try {
            repository.update(personOne);
            fail();
        } catch (PersonCollisionException e) {
            assertEquals(EMAIL_EXISTS, e.getMessage());
        }

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void update_withUsernameCollision() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        personOne.setUsername(USERNAME2);
        try {
            repository.update(personOne);
            fail();
        } catch (PersonCollisionException e) {
            assertEquals(USERNAME_EXISTS, e.getMessage());
        }

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void update_withDniCollision() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        personOne.setDni(DNI2);
        try {
            repository.update(personOne);
            fail();
        } catch (PersonCollisionException e) {
            assertEquals(DNI_EXISTS, e.getMessage());
        }

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void delete() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        repository.delete(personId);

        Optional<Person> possiblePerson = repository.findById(personId);
        assertFalse(possiblePerson.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void delete_null_returnsEmpty() throws Exception {
        repository.delete(null);
    }
}