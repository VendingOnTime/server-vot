package unit.com.vendingontime.backend.models.bodymodels;

import com.vendingontime.backend.models.PersonRole;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.utils.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.vendingontime.backend.models.bodymodels.SignUpData.*;
import static org.junit.Assert.*;

/**
 * Created by alberto on 16/3/17.
 */
public class SignUpDataTest {
    private SignUpData customer;
    private SignUpData supervisor;
    private SignUpData technician;

    @Before
    public void setUp() throws Exception {
        customer = new SignUpData();
        customer.setRole(PersonRole.CUSTOMER);

        supervisor = new SignUpData();
        supervisor.setRole(PersonRole.SUPERVISOR);
        supervisor.setEmail("user@example.com");
        supervisor.setUsername("user");
        supervisor.setPassword("12345");
        supervisor.setName("name");
        supervisor.setSurnames("surnames");

        technician = new SignUpData();
        technician.setRole(PersonRole.TECHNICIAN);
        technician.setEmail("user@example.com");
        technician.setUsername("user");
        technician.setPassword("12345");
        technician.setName("name");
        technician.setSurnames("surnames");
        technician.setDni("11111111A");
    }

    @After
    public void tearDown() throws Exception {
        customer = null;
        supervisor = null;
        technician = null;
    }

    @Test
    public void validate_noRole_error() throws Exception {
        assertArrayEquals(new String[]{EMPTY_ROLE}, new SignUpData().validate());
    }

    @Test
    public void validate_customer_noErrors() throws Exception {
        assertArrayEquals(new String[]{}, customer.validate());
    }

    @Test
    public void validate_user_invalidEmail() throws Exception {
        customer.setEmail("aaaa");
        assertArrayEquals(new String[]{INVALID_EMAIL}, customer.validate());
    }

    @Test
    public void validate_user_invalidDni() throws Exception {
        customer.setDni("aaaa");
        assertArrayEquals(new String[]{INVALID_DNI}, customer.validate());
    }

    @Test
    public void validate_user_invalidUsername() throws Exception {
        customer.setUsername("user@");
        assertArrayEquals(new String[]{INVALID_USERNAME}, customer.validate());
    }

    @Test
    public void validate_user_shortPassword() throws Exception {
        customer.setPassword(StringUtils.createFilled(MIN_PASSWORD_LENGTH - 1));
        assertArrayEquals(new String[]{SHORT_PASSWORD}, customer.validate());
    }

    @Test
    public void validate_user_shortUsername() throws Exception {
        customer.setUsername(StringUtils.createFilled(MIN_USERNAME_LENGTH - 1));
        assertArrayEquals(new String[]{SHORT_USERNAME}, customer.validate());
    }

    @Test
    public void validate_user_longUsername() throws Exception {
        customer.setUsername(StringUtils.createFilled(MAX_USERNAME_LENGTH + 1));
        assertArrayEquals(new String[]{LONG_USERNAME}, customer.validate());
    }

    @Test
    public void validate_supervisor_noErrors() throws Exception {
        assertArrayEquals(new String[]{}, supervisor.validate());
    }

    @Test
    public void validate_supervisor_allErrors() throws Exception {
        SignUpData supervisor = new SignUpData();
        supervisor.setRole(PersonRole.SUPERVISOR);

        assertArrayEquals(new String[]{
                EMPTY_EMAIL, EMPTY_USERNAME, EMPTY_PASSWORD, EMPTY_NAME, EMPTY_SURNAMES
        }, supervisor.validate());
    }

    @Test
    public void validate_technician_noErrors() throws Exception {
        assertArrayEquals(new String[]{}, technician.validate());
    }

    @Test
    public void validate_technician_allErrors() throws Exception {
        SignUpData technician = new SignUpData();
        technician.setRole(PersonRole.TECHNICIAN);

        assertArrayEquals(new String[]{
                EMPTY_EMAIL, EMPTY_USERNAME, EMPTY_PASSWORD, EMPTY_NAME, EMPTY_SURNAMES, EMPTY_DNI
        }, technician.validate());
    }
}