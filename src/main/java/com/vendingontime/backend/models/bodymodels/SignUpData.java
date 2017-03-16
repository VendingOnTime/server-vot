package com.vendingontime.backend.models.bodymodels;

import com.vendingontime.backend.models.PersonRole;
import lombok.Data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.vendingontime.backend.utils.StringUtils.*;

/**
 * Created by Alberto on 13/03/2017.
 */
@Data
public class SignUpData implements Validable {

    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MIN_PASSWORD_LENGTH = 5;

    public static final int MAX_USERNAME_LENGTH = 20;

    public static final String EMPTY_EMAIL = "EMPTY_EMAIL";
    public static final String EMPTY_USERNAME = "EMPTY_USERNAME";
    public static final String EMPTY_PASSWORD = "EMPTY_PASSWORD";
    public static final String EMPTY_DNI = "EMPTY_DNI";
    public static final String EMPTY_NAME = "EMPTY_NAME";
    public static final String EMPTY_SURNAMES = "EMPTY_SURNAMES";
    public static final String EMPTY_ROLE = "EMPTY_ROLE";

    public static final String INVALID_EMAIL = "INVALID_EMAIL";
    public static final String INVALID_DNI = "INVALID_DNI";
    public static final String INVALID_USERNAME = "INVALID_USERNAME";
    public static final String INVALID_ROLE = "INVALID_ROLE";

    public static final String SHORT_USERNAME = "SHORT_USERNAME";
    public static final String SHORT_PASSWORD = "SHORT_PASSWORD";

    public static final String LONG_USERNAME = "LONG_USERNAME";

    private String email;
    private String username;
    private String password;
    private String dni;
    private String name;
    private String surnames;
    private PersonRole role;

    @Override
    public String[] validate() {
        List<String> causes = validateDependingOnRole();
        return causes.toArray(new String[causes.size()]);
    }

    private List<String> validateDependingOnRole() {
        if (role == null) return Collections.singletonList(EMPTY_ROLE);

        switch (role) {
            case CUSTOMER:
                return validateCustomer();
            case SUPERVISOR:
                return validateSupervisor();
            case TECHNICIAN:
                return validateTechnician();
            default:
                return Collections.singletonList(INVALID_ROLE);
        }
    }

    private List<String> validateCustomer() {
        return validateCommon();
    }

    private List<String> validateSupervisor() {
        List<String> causes = new LinkedList<>();
        causes.addAll(validateCommon());
        causes.addAll(validateBusinessCommon());
        return causes;
    }

    private List<String> validateTechnician() {
        List<String> causes = new LinkedList<>();
        causes.addAll(validateCommon());
        causes.addAll(validateBusinessCommon());
        if (isEmpty(dni)) causes.add(EMPTY_DNI);
        return causes;
    }

    private List<String> validateBusinessCommon() {
        List<String> causes = new LinkedList<>();
        if (isEmpty(email)) causes.add(EMPTY_EMAIL);
        if (isEmpty(username)) causes.add(EMPTY_USERNAME);
        if (isEmpty(password)) causes.add(EMPTY_PASSWORD);
        if (isEmpty(name)) causes.add(EMPTY_NAME);
        if (isEmpty(surnames)) causes.add(EMPTY_SURNAMES);
        return causes;
    }

    private List<String> validateCommon() {
        List<String> causes = new LinkedList<>();
        if (!isEmpty(email) && !isEmail(email)) causes.add(INVALID_EMAIL);
        if (!isEmpty(username) && !isValidUsername(username)) causes.add(INVALID_USERNAME);
        if (!isEmpty(username) && isShort(username, MIN_USERNAME_LENGTH)) causes.add(SHORT_USERNAME);
        if (!isEmpty(username) && isLong(username, MAX_USERNAME_LENGTH)) causes.add(LONG_USERNAME);
        if (!isEmpty(password) && isShort(password, MIN_PASSWORD_LENGTH)) causes.add(SHORT_PASSWORD);
        if (!isEmpty(dni) && !isDni(dni)) causes.add(INVALID_DNI);
        return causes;
    }
}
