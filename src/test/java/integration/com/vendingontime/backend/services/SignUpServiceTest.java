package integration.com.vendingontime.backend.services;

import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.repositories.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by alberto on 28/3/17.
 */
public class SignUpServiceTest extends IntegrationTest {

    @Inject
    SignUpService service;

    @Inject
    PersonRepository repository;

    private SignUpData supervisor;

    @Before
    public void setUp() throws Exception {
        supervisor = new SignUpData()
                .setRole(PersonRole.SUPERVISOR)
                .setEmail("user@example.com")
                .setUsername("user")
                .setPassword("12345")
                .setName("name")
                .setSurnames("surnames");

    }

    @After
    public void tearDown() throws Exception {
        supervisor = null;
    }

    @Test
    public void createUser() throws Exception {
        Person user = service.createSupervisor(supervisor);

        assertNotNull(user);

        Optional<Person> byEmail = repository.findByEmail(supervisor.getEmail());
        assertTrue(byEmail.isPresent());

        repository.delete(user.getId());
    }

}