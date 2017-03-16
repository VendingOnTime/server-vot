package com.vendingontime.backend.models;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Alberto on 14/03/2017.
 */
public class PersonCollisionException extends RuntimeException {
    public static final String EMAIL_EXISTS = "EMAIL_EXISTS";
    public static final String USERNAME_EXISTS = "USERNAME_EXISTS";
    public static final String DNI_EXISTS = "DNI_EXISTS";

    private final String[] causes;

    public PersonCollisionException(Cause[] errors) {
        super();
        this.causes = Arrays.stream(errors)
                .map(error -> error.cause)
                .collect(Collectors.toList())
                .toArray(new String[errors.length]);
    }

    public enum Cause {
        EMAIL(EMAIL_EXISTS),
        USERNAME(USERNAME_EXISTS),
        DNI(DNI_EXISTS);

        private final String cause;

        Cause(String cause) {
            this.cause = cause;
        }
    }

    public String[] getCauses() {
        return causes;
    }
}
