package testutils;

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

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.bodymodels.person.EditPasswordData;
import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.location.MachineLocation;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.machine.MachineType;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;

public class FixtureFactory {
    private static final FixtureFactory instance = new FixtureFactory();

    // COMPANY METHODS
    public static Company generateCompany() {
        return instance.generateBasicCompany();
    }

    public static Company generateCompanyWithOwner() {
        return instance.generateBasicCompany().setOwner(generateSupervisor());
    }

    private Company generateBasicCompany() {
        return new Company();
    }

    // PERSON METHODS

    public static Person generateSupervisor() {
        return new Person(generateSignUpData())
                .setRole(PersonRole.SUPERVISOR);
    }

    public static Person generateSupervisorWithCompany() {
        return generateCompanyWithOwner().getOwner();
    }

    public static Person generateTechnician() {
        return new Person(generateSignUpData())
                .setRole(PersonRole.TECHNICIAN);
    }

    public static Person generateCustomer() {
        return new Person(generateSignUpData())
                .setRole(PersonRole.CUSTOMER);
    }

    public static SignUpData generateSignUpData() {
        return new SignUpData()
                .setDni("11111111A")
                .setName("NAME")
                .setSurnames("SURNAME")
                .setEmail("person@example.com")
                .setUsername("USERNAME")
                .setPassword("PASSWORD");
    }

    public static LogInData generateLogInData() {
        return new LogInData()
                .setEmail("person@example.com")
                .setPassword("PASSWORD");
    }

    public static LogInData generateLogInDataFrom(Person person) {
        return new LogInData()
                .setEmail(person.getEmail())
                .setPassword(person.getPassword());
    }

    public static LogInData generateLogInDataFrom(SignUpData signUpData) {
        return new LogInData()
                .setEmail(signUpData.getEmail())
                .setPassword(signUpData.getPassword());
    }

    public static EditPersonData generateEditPersonData() {
        SignUpData signUpData = generateSignUpData();

        return (EditPersonData) new EditPersonData()
                .setDni(signUpData.getDni())
                .setName(signUpData.getName())
                .setSurnames(signUpData.getSurnames())
                .setEmail(signUpData.getEmail())
                .setUsername(signUpData.getUsername())
                .setPassword(signUpData.getPassword())
                .setRole(PersonRole.SUPERVISOR);
    }

    public static EditPersonData generateEditPersonDataFrom(Person person) {
        return (EditPersonData) new EditPersonData()
                .setId(person.getId())
                .setDni(person.getDni())
                .setName(person.getName())
                .setSurnames(person.getSurnames())
                .setEmail(person.getEmail())
                .setUsername(person.getUsername())
                .setPassword(person.getPassword())
                .setRole(person.getRole());
    }

    public static EditPasswordData generateEditPasswordDataFrom(Person person) {
        return new EditPasswordData()
                .setId(person.getId())
                .setOldPassword(person.getPassword());
    }

    // MACHINE METHODS

    public static Machine generateMachine() {
        return new Machine(generateAddMachineData());
    }

    public static AddMachineData generateAddMachineData() {
        return new AddMachineData()
                .setDescription("DESCRIPTION")
                .setType(MachineType.COFFEE)
                .setState(MachineState.OPERATIVE)
                .setLocation(instance.generateBasicLocation());
    }

    public static EditMachineData generateEditMachineData() {
        AddMachineData amd = generateAddMachineData();

        return (EditMachineData) new EditMachineData()
                .setDescription(amd.getDescription())
                .setType(amd.getType())
                .setState(amd.getState())
                .setLocation(amd.getLocation());
    }

    public static EditMachineData generateEditMachineDataFrom(Machine machine) {
        EditMachineData editMachineData = new EditMachineData();

        editMachineData.setId(machine.getId());
        editMachineData.setDescription(machine.getDescription());
        editMachineData.setLocation(machine.getLocation());
        editMachineData.setState(machine.getState());
        editMachineData.setType(machine.getType());

        return editMachineData;
    }

    // MACHINE LOCATION METHODS

    private MachineLocation generateBasicLocation() {
        return new MachineLocation().setName("LOCATION_NAME");
    }
}
