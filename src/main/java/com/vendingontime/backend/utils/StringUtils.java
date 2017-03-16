package com.vendingontime.backend.utils;

/**
 * Created by Alberto on 13/03/2017.
 */
public class StringUtils {

    private static String EMAIL_REGEX =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isEmail(String string) {
        if (isEmpty(string)) return false;
        return string.matches(EMAIL_REGEX);
    }
}
