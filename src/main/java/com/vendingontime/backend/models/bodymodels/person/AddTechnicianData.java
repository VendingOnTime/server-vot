package com.vendingontime.backend.models.bodymodels.person;

import com.vendingontime.backend.models.person.Person;

import java.util.Arrays;
import java.util.LinkedList;

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
public class AddTechnicianData extends SignUpData {
    public static final String EMPTY_REQUESTER = "EMPTY_REQUESTER";

    private Person requester;

    @Override
    public String[] validate() {
        LinkedList<String> causes = new LinkedList<>(Arrays.asList(super.validate()));

        if (requester == null) causes.add(EMPTY_REQUESTER);

        return causes.toArray(new String[causes.size()]);
    }

    public Person getRequester() {
        return requester;
    }

    public AddTechnicianData setRequester(Person requester) {
        this.requester = requester;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddTechnicianData)) return false;
        if (!super.equals(o)) return false;

        AddTechnicianData that = (AddTechnicianData) o;

        return getRequester() != null ? getRequester().equals(that.getRequester()) : that.getRequester() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getRequester() != null ? getRequester().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddTechnicianData{" +
                "requester=" + requester +
                "} " + super.toString();
    }
}
