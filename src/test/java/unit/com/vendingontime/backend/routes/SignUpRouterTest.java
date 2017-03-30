package unit.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.vendingontime.backend.models.bodymodels.SignUpData.INVALID_DNI;
import static com.vendingontime.backend.routes.SignUpRouter.MALFORMED_JSON;
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
    private ServiceResponse serviceResponse;
    private SignUpRouter signUp;
    private SignUpData payload;

    private Person person;
    private String stringifiedPerson;

    @Before
    public void setUp() throws Exception {
        service = mock(SignUpService.class);
        serviceResponse = mock(ServiceResponse.class);
        signUp = new SignUpRouter(service, serviceResponse);

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
        serviceResponse = null;
        signUp = null;
        payload = null;
        stringifiedPerson = null;
    }

    @Test
    public void signUpSupervisor() {
        when(service.createSupervisor(payload)).thenReturn(person);
        signUp.signUpSupervisor(stringifiedPerson);

        verify(serviceResponse, times(1)).created(person);
        verify(service, times(1)).createSupervisor(payload);
    }

    @Test
    public void signUpSupervisor_withInvalidData() {
        doThrow(new BusinessLogicException(new String[]{INVALID_DNI}))
                .when(service).createSupervisor(payload);
        signUp.signUpSupervisor(stringifiedPerson);

        verify(service, times(1)).createSupervisor(payload);
        verify(serviceResponse, times(1)).badRequest(any());
    }

    @Test
    public void signUpSupervisor_withEmptyJSON() {
        stringifiedPerson = "";

        signUp.signUpSupervisor(stringifiedPerson);

        verify(serviceResponse, never()).created(person);
        verify(serviceResponse, times(1)).badRequest(any());
        verify(service, never()).createSupervisor(payload);
    }

    @Test
    public void signUpSupervisor_withInvalidJSONField() {
        stringifiedPerson = "{\"id\":\"1234\"}";

        signUp.signUpSupervisor(stringifiedPerson);

        verify(serviceResponse, never()).created(person);
        verify(serviceResponse, times(1)).badRequest(MALFORMED_JSON);
        verify(service, never()).createSupervisor(payload);
    }
}