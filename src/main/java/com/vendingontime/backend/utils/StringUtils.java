package com.vendingontime.backend.utils;

import java.util.regex.Pattern;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class StringUtils {

    private static Pattern EMAIL_PATTERN =
            Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private static Pattern DNI_PATTERN =
            Pattern.compile("^([x-zX-Z])([-]?)(\\d{8})([-]?)([a-zA-Z])|(\\d{8})([-]?)([a-zA-Z])$");

    private static Pattern VALID_USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9-_.]{1,20}$");

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isEmail(String string) {
        if (isEmpty(string)) return false;
        return EMAIL_PATTERN.matcher(string).matches();
    }

    public static boolean isDni(String string) {
        if (isEmpty(string)) return false;
        return DNI_PATTERN.matcher(string).matches();
    }

    public static boolean isValidUsername(String string) {
        if (isEmpty(string)) return false;
        return VALID_USERNAME_PATTERN.matcher(string).matches();
    }

    public static boolean isShort(String string, int length) {
        if (length == 0 && string != null && isEmpty(string)) return false;
        if (isEmpty(string)) return true;
        return string.length() < length;
    }

    public static boolean isLong(String string, int length) {
        if (isEmpty(string)) return false;
        return string.length() > length;
    }

    public static String createFilled(int length) {
        return new String(new char[length]).replace('\0', 'a');
    }
}
