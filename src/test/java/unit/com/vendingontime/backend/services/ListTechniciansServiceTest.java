package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.ListMachinesService;
import com.vendingontime.backend.services.ListTechniciansService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
public class ListTechniciansServiceTest {
    private PersonRepository repository;
    private ListTechniciansService listTechniciansService;

    private Person owner;
    private Person customer;
    private Person technician;
    private Company company;

    @Before
    public void setUp() throws Exception {
        repository = mock(PersonRepository.class);
        listTechniciansService = new ListTechniciansService(repository);

        company = FixtureFactory.generateCompanyWithOwner();
        owner = company.getOwner();
        customer = FixtureFactory.generateCustomer();
        technician = FixtureFactory.generateTechnician();
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        listTechniciansService = null;
        company = null;
        owner = null;
        customer = null;
        technician = null;
    }

    @Test
    public void listFor_companyWithTechnicians_returnsListOfTechnicians() throws Exception {
        company.addWorker(new Person().setId("TECHNICIAN_1"));
        company.addWorker(new Person().setId("TECHNICIAN_2"));

        when(repository.findTechniciansByCompany(company)).thenReturn(new LinkedList<>(company.getWorkers()));

        List<Person> technicians = listTechniciansService.listFor(owner);
        assertThat(technicians, equalTo(new LinkedList<>(company.getWorkers())));
    }

    @Test
    public void listFor_companyWithNoTechnicians_returnsEmptyList() throws Exception {
        Set<Person> workersWithoutOwner = new HashSet<>(company.getWorkers());
        workersWithoutOwner.remove(company.getOwner());

        when(repository.findTechniciansByCompany(company)).thenReturn(new LinkedList<>(workersWithoutOwner));

        List<Person> technicians = listTechniciansService.listFor(owner);
        assertThat(technicians.size(), is(0));
    }

    @Test
    public void listFor_customer_returnsInsufficientPermissions() throws Exception {
        checkInsuffientPermissions(customer);
    }

    @Test
    public void listFor_technician_returnsInsufficientPermissions() throws Exception {
        checkInsuffientPermissions(technician);
    }

    @Test(expected = NullPointerException.class)
    public void listFor_nullPerson_throwsException() throws Exception {
        listTechniciansService.listFor(null);
    }

    @Test(expected = NullPointerException.class)
    public void listFor_unknownRole_throwsException() throws Exception {
        listTechniciansService.listFor(new Person());
    }

    private void checkInsuffientPermissions(Person person) {
        try {
            listTechniciansService.listFor(person);
            fail();
        } catch (BusinessLogicException e) {
            verify(repository, never()).findTechniciansByCompany(any());
            assertThat(e.getCauses(), equalTo(new String[]{ListMachinesService.INSUFFICIENT_PERMISSIONS}));
        }
    }
}