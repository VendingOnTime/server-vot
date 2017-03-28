package com.vendingontime.backend.services.utils;

/**
 * Created by miguel on 28/3/17.
 */
public class DummyPasswordEncryptor implements PasswordEncryptor {
    @Override
    public String encrypt(String password) {
        return password;
    }

    @Override
    public boolean check(String encryptedPwd, String providedPwd) {
        return encryptedPwd.equals(providedPwd);
    }
}
