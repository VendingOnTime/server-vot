package unit.com.vendingontime.backend.models.bodymodels;

import com.vendingontime.backend.models.bodymodels.person.LogInData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.vendingontime.backend.models.bodymodels.person.LogInData.*;

/**
 * Created by miguel on 3/26/17.
 */
public class LogInDataTest {
    public LogInData user;

    @Before
    public void setUp() throws Exception {
        user = new LogInData();

        user.setEmail("user@example.com");
        user.setPassword("12345");
    }

    @After
    public void tearDown() throws Exception {
        user = null;
    }

    @Test
    public void validate_user_noErrors() {
        assertArrayEquals(new String[]{}, user.validate());
    }

    @Test
    public void validate_invalidMail_error() throws Exception {
        user.setEmail("aaaa");
        assertArrayEquals(new String[]{BAD_LOGIN}, user.validate());
    }

    @Test
    public void validate_invalidPassword_error() throws Exception {
        user.setPassword("");
        assertArrayEquals(new String[]{BAD_LOGIN}, user.validate());
    }

    @Test
    public void validate_user_allErrors() throws Exception {
        user.setEmail("aaaa").setPassword("");
        assertArrayEquals(new String[]{BAD_LOGIN}, user.validate());
    }

}