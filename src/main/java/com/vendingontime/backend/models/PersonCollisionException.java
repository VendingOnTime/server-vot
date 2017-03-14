package com.vendingontime.backend.models;

import lombok.Getter;

/**
 * Created by Alberto on 14/03/2017.
 */
public class PersonCollisionException extends RuntimeException {
    public static final String EMAIL_EXISTS = "EMAIL_EXISTS";
    public static final String USERNAME_EXISTS = "USERNAME_EXISTS";
    public static final String DNI_EXISTS = "DNI_EXISTS";

    public PersonCollisionException(Cause cause) {
        super(cause.getCause());
    }

    public enum Cause {
        EMAIL(EMAIL_EXISTS),
        USERNAME(USERNAME_EXISTS),
        DNI(DNI_EXISTS);

        private final @Getter String cause;

        Cause(String cause) {
            this.cause = cause;
        }
    }
}
