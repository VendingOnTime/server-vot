package com.vendingontime.backend.models.bodymodels.machine;

import com.vendingontime.backend.utils.StringUtils;

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
public class EditMachineData extends AddMachineData {

    public static final String EMPTY_MACHINE_ID = "EMPTY_MACHINE_ID";

    private String id;

    @Override
    public String[] validate() {
        LinkedList<String> causes = new LinkedList<>(Arrays.asList(super.validate()));

        if (StringUtils.isEmpty(id)) causes.add(EMPTY_MACHINE_ID);

        return causes.toArray(new String[causes.size()]);
    }

    public String getId() {
        return id;
    }

    public EditMachineData setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditMachineData)) return false;
        if (!super.equals(o)) return false;

        EditMachineData that = (EditMachineData) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EditMachineData{" +
                "id='" + id + '\'' +
                "} " + super.toString();
    }
}
