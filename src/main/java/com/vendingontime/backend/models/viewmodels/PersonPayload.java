package com.vendingontime.backend.models.viewmodels;

import com.vendingontime.backend.models.PersonRole;
import lombok.Data;

import static com.vendingontime.backend.utils.StringUtils.isEmpty;

/**
 * Created by Alberto on 13/03/2017.
 */
@Data
public class PersonPayload implements Validable {

    private String email;
    private String username;
    private String password;
    private String dni;
    private String name;
    private String surnames;
    private PersonRole role;

    @Override
    public boolean isValid() {
        switch (role) {
            case CUSTOMER:
                return validateCustomer();
            case SUPERVISOR:
                return validateSupervisor();
            case TECHNICIAN:
                return validateTechnician();
            default:
                return false;
        }
    }

    private boolean validateCustomer() {
        return true;
    }

    private boolean validateSupervisor() {
        return validateCommon();
    }

    private boolean validateTechnician() {
        return validateCommon();
    }

    private boolean validateCommon() {
        if (isEmpty(email)) return false;
        if (isEmpty(username)) return false;
        if (isEmpty(password)) return false;
        if (isEmpty(dni)) return false;
        if (isEmpty(name)) return false;
        if (isEmpty(surnames)) return false;
        return true;
    }
}
