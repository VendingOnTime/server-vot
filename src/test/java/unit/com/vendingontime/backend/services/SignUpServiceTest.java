package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.Person;

import com.vendingontime.backend.models.PersonCollisionException;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.services.SignUpService;

import com.vendingontime.backend.services.utils.BusinessLogicException;

import static com.vendingontime.backend.models.PersonCollisionException.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by miguel on 13/3/17.
 */
public class SignUpServiceTest {
    private static final String DNI = "12345678B";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "username@test.com";
    private static final String NAME = "NAME";
    private static final String SURNAME = "SURNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final PersonRole ROLE = PersonRole.SUPERVISOR;

    private JPAPersonRepository repository;
    private SignUpService signUp;
    private SignUpData payload;

    private Person person;

    @Before
    public void setUp() throws Exception {
        repository = mock(JPAPersonRepository.class);
        signUp = new SignUpService(repository);

        payload = new SignUpData()
                .setDni(DNI)
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setName(NAME)
                .setSurnames(SURNAME)
                .setPassword(PASSWORD)
                .setRole(ROLE);

        person = new Person(payload);

    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        signUp = null;
        payload = null;
    }

    @Test
    public void createUser() {
        signUp.createUser(payload);

        verify(repository, times(1)).create(person);
    }

    @Test
    public void createUser_withInvalidPayload_throwsException() throws Exception {
        try {
            payload.setEmail("");
            signUp.createUser(payload);
            fail();
        } catch (BusinessLogicException e) {
            assertArrayEquals(new String[]{SignUpData.EMPTY_EMAIL}, e.getCauses());
        }
    }

    @Test
    public void createUser_withCollision_throwsException() throws Exception {
        try {
            doThrow(new PersonCollisionException(new Cause[]{Cause.EMAIL}))
                    .when(repository).create(any());
            signUp.createUser(payload);
            fail();
        } catch (BusinessLogicException e) {
            assertArrayEquals(new String[]{EMAIL_EXISTS}, e.getCauses());
        }
    }
}