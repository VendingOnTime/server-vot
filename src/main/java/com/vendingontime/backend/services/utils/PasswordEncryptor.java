package com.vendingontime.backend.services.utils;

/**
 * Created by miguel on 28/3/17.
 */
public interface PasswordEncryptor {
    String encrypt(String password);
    boolean check(String encryptedPwd, String providedPwd);
}
