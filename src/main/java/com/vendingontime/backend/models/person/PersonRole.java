package com.vendingontime.backend.models.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Alberto on 13/03/2017.
 */
public enum PersonRole {
    CUSTOMER, SUPERVISOR, TECHNICIAN;

    @JsonCreator
    public static PersonRole fromValue(String value) {
        return PersonRole.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return toString().toLowerCase();
    }
}
