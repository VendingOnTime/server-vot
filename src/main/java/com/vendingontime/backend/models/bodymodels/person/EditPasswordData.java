package com.vendingontime.backend.models.bodymodels.person;

import com.vendingontime.backend.models.bodymodels.Validable;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.vendingontime.backend.models.bodymodels.person.SignUpData.MIN_PASSWORD_LENGTH;
import static com.vendingontime.backend.utils.StringUtils.*;
import static com.vendingontime.backend.utils.StringUtils.isEmpty;

/**
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
public class EditPasswordData implements Validable {

    public static final String EMPTY_PERSON_ID = "EMPTY_PERSON_ID";
    public static final String EMPTY_OLD_PASSWORD = "EMPTY_OLD_PASSWORD";
    public static final String EMPTY_NEW_PASSWORD = "EMPTY_NEW_PASSWORD";
    public static final String SHORT_NEW_PASSWORD = "SHORT_NEW_PASSWORD";
    public static final String EMPTY_REQUESTER = "EMPTY_REQUESTER";

    private String id;
    private String oldPassword;
    private String newPassword;
    private Person requester;

    @Override
    public String[] validate() {
        List<String> causes = new LinkedList<>();

        if (StringUtils.isEmpty(id)) causes.add(EMPTY_PERSON_ID);
        if (isEmpty(oldPassword)) causes.add(EMPTY_OLD_PASSWORD);
        // FIXME: alberto@7/5/17 Define business domain objects to represent passwords, emails, etc.
        if (isEmpty(newPassword)) causes.add(EMPTY_NEW_PASSWORD);
        else if (isShort(newPassword, MIN_PASSWORD_LENGTH)) causes.add(SHORT_NEW_PASSWORD);
        if (requester == null || requester.getRole() == null) causes.add(EMPTY_REQUESTER);

        return causes.toArray(new String[causes.size()]);
    }

    public String getId() {
        return id;
    }

    public EditPasswordData setId(String id) {
        this.id = id;
        return this;
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

    public Person getRequester() {
        return requester;
    }

    public EditPasswordData setRequester(Person requester) {
        this.requester = requester;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditPasswordData)) return false;

        EditPasswordData that = (EditPasswordData) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getOldPassword() != null ? !getOldPassword().equals(that.getOldPassword()) : that.getOldPassword() != null)
            return false;
        if (getNewPassword() != null ? !getNewPassword().equals(that.getNewPassword()) : that.getNewPassword() != null)
            return false;
        return getRequester() != null ? getRequester().equals(that.getRequester()) : that.getRequester() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getOldPassword() != null ? getOldPassword().hashCode() : 0);
        result = 31 * result + (getNewPassword() != null ? getNewPassword().hashCode() : 0);
        result = 31 * result + (getRequester() != null ? getRequester().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EditPasswordData{" +
                "id='" + id + '\'' +
                ", requester=" + requester +
                '}';
    }
}
