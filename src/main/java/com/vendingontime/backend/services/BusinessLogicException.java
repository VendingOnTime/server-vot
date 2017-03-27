package com.vendingontime.backend.services;

/**
 * Created by miguel on 27/3/17.
 */
public class BusinessLogicException extends RuntimeException {
    private final String[] causes;

    public BusinessLogicException(String[] causes) {
        this.causes = causes;
    }

    public String[] getCauses() {
        return causes;
    }
}
