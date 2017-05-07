package com.vendingontime.backend.models.bodymodels.person;
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

import com.vendingontime.backend.models.bodymodels.Validable;

import java.util.LinkedList;
import java.util.List;

import static com.vendingontime.backend.models.bodymodels.person.SignUpData.MIN_PASSWORD_LENGTH;
import static com.vendingontime.backend.utils.StringUtils.*;
import static com.vendingontime.backend.utils.StringUtils.isEmpty;

public class EditPasswordData implements Validable {

    public static final String EMPTY_OLD_PASSWORD = "EMPTY_OLD_PASSWORD";
    public static final String EMPTY_NEW_PASSWORD = "EMPTY_NEW_PASSWORD";
    public static final String SHORT_NEW_PASSWORD = "SHORT_NEW_PASSWORD";

    private String oldPassword;
    private String newPassword;

    @Override
    public String[] validate() {
        List<String> causes = new LinkedList<>();

        if (isEmpty(oldPassword)) causes.add(EMPTY_OLD_PASSWORD);

        // FIXME: alberto@7/5/17 Define business domain objects to represent passwords, emails, etc.
        if (isEmpty(newPassword)) causes.add(EMPTY_NEW_PASSWORD);
        if (!isEmpty(newPassword) && isShort(newPassword, MIN_PASSWORD_LENGTH)) causes.add(SHORT_NEW_PASSWORD);

        return causes.toArray(new String[causes.size()]);
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public EditPasswordData setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        return this;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public EditPasswordData setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditPasswordData)) return false;

        EditPasswordData that = (EditPasswordData) o;

        if (getOldPassword() != null ? !getOldPassword().equals(that.getOldPassword()) : that.getOldPassword() != null)
            return false;
        return getNewPassword() != null ? getNewPassword().equals(that.getNewPassword()) : that.getNewPassword() == null;
    }

    @Override
    public int hashCode() {
        int result = getOldPassword() != null ? getOldPassword().hashCode() : 0;
        result = 31 * result + (getNewPassword() != null ? getNewPassword().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EditPasswordData{" +
                "oldPassword='" + oldPassword + '\'' + // TODO: alberto@7/5/17 Decide if we should print this
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
