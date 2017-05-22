package integration.com.vendingontime.backend.repositories;

import com.google.inject.Inject;

import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonCollisionException;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.*;
import testutils.FixtureFactory;

import java.util.List;
import java.util.Optional;

import static com.vendingontime.backend.models.person.PersonCollisionException.DNI_EXISTS;
import static com.vendingontime.backend.models.person.PersonCollisionException.EMAIL_EXISTS;
import static com.vendingontime.backend.models.person.PersonCollisionException.USERNAME_EXISTS;
import static junit.framework.TestCase.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

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
public class JPAPersonRepositoryTest extends IntegrationTest {

    private static final String DNI2 = "DNI2";
    private static final String USERNAME2 = "USERNAME2";
    private static final String EMAIL2 = "EMAIL2";

    @Inject private JPAPersonRepository repository;
    @Inject private CompanyRepository companyRepository;

    private Person personOne;
    private Person personTwo;

    @Before
    public void setUp() throws Exception {
        SignUpData payload = FixtureFactory.generateSignUpData().setRole(PersonRole.SUPERVISOR);

        personOne = new Person(payload);
        personTwo = new Person(payload)
                .setDni(DNI2)
                .setEmail(EMAIL2)
                .setUsername(USERNAME2);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        personOne = null;
        personTwo = null;
    }

    @Test
    public void create() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findById(personId);
        assertEquals(person, possiblePerson.get());

        repository.delete(personId);
    }

    @Test
    public void create_twoWithoutCollisions() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        Optional<Person> possiblePersonOne = repository.findById(personOneId);
        assertEquals(personOne, possiblePersonOne.get());
        Optional<Person> possiblePersonTwo = repository.findById(personTwoId);
        assertEquals(personTwo, possiblePersonTwo.get());

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void create_withEmailCollision() throws Exception {
        Person person = repository.create(personOne);
        String personId = person.getId();

        personTwo.setEmail(personOne.getEmail());

        try {
            repository.create(personTwo);
            fail();
        } catch (PersonCollisionException e) {
            assertArrayEquals(new String[]{EMAIL_EXISTS}, e.getCauses());
        }

        repository.delete(personId);
    }

    @Test
    public void create_withUsernameCollision() throws Exception {
        Person person = repository.create(personOne);
        String personId = person.getId();

        personTwo.setUsername(personOne.getUsername());

        try {
            repository.create(personTwo);
            fail();
        } catch (PersonCollisionException e) {
            assertArrayEquals(new String[]{USERNAME_EXISTS}, e.getCauses());
        }

        repository.delete(personId);
    }

    @Test
    public void create_withDniCollision() throws Exception {
        Person person = repository.create(personOne);
        String personId = person.getId();

        personTwo.setDni(personOne.getDni());

        try {
            repository.create(personTwo);
            fail();
        } catch (PersonCollisionException e) {
            assertArrayEquals(new String[]{DNI_EXISTS}, e.getCauses());
        }

        repository.delete(personId);
    }

    @Test
    public void create_withAllCollisions() throws Exception {
        Person person = repository.create(personOne);
        String personId = person.getId();

        personTwo.setEmail(personOne.getEmail());
        personTwo.setUsername(personOne.getUsername());
        personTwo.setDni(personOne.getDni());

        try {
            repository.create(personTwo);
            fail();
        } catch (PersonCollisionException e) {
            assertArrayEquals(new String[]{EMAIL_EXISTS, USERNAME_EXISTS, DNI_EXISTS}, e.getCauses());
        }

        repository.delete(personId);
    }

    @Test
    public void findById() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findById(personId);
        assertTrue(possiblePerson.isPresent());
        assertEquals(personId, possiblePerson.get().getId());

        repository.delete(personId);
    }

    @Test
    public void findById_null_returnsEmpty() throws Exception {
        Optional<Person> possiblePerson = repository.findById(null);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void findByEmail() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findByEmail(personOne.getEmail());
        assertTrue(possiblePerson.isPresent());
        assertEquals(personOne.getEmail(), possiblePerson.get().getEmail());

        repository.delete(personId);
    }

    @Test
    public void findByEmail_null_returnsEmpty() throws Exception {
        Optional<Person> possiblePerson = repository.findByEmail(null);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void findByUsername() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findByUsername(personOne.getUsername());
        assertTrue(possiblePerson.isPresent());
        assertEquals(personOne.getUsername(), possiblePerson.get().getUsername());

        repository.delete(personId);
    }

    @Test
    public void findByUsername_null_returnsEmpty() throws Exception {
        Optional<Person> possiblePerson = repository.findByUsername(null);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void findByDni() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possiblePerson = repository.findByDni(personOne.getDni());
        assertTrue(possiblePerson.isPresent());
        assertEquals(personOne.getDni(), possiblePerson.get().getDni());

        repository.delete(personId);
    }

    @Test
    public void findByDni_null_returnsEmpty() throws Exception {
        Optional<Person> possiblePerson = repository.findByDni(null);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void update() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        person.setDni(DNI2);
        repository.update(person);

        Optional<Person> possiblePerson = repository.findById(personId);
        assertEquals(DNI2, possiblePerson.get().getDni());

        repository.delete(personId);
    }

    @Test(expected = NullPointerException.class)
    public void update_null_returnsEmpty() throws Exception {
        repository.update(null);
    }

    @Test
    public void update_withEmailCollision() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        personOne.setEmail(EMAIL2);
        try {
            repository.update(personOne);
            fail();
        } catch (PersonCollisionException e) {
            assertArrayEquals(new String[]{EMAIL_EXISTS}, e.getCauses());
        }

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void update_withUsernameCollision() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        personOne.setUsername(USERNAME2);
        try {
            repository.update(personOne);
            fail();
        } catch (PersonCollisionException e) {
            assertArrayEquals(new String[]{USERNAME_EXISTS}, e.getCauses());
        }

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void update_withDniCollision() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        personOne.setDni(DNI2);
        try {
            repository.update(personOne);
            fail();
        } catch (PersonCollisionException e) {
            assertArrayEquals(new String[]{DNI_EXISTS}, e.getCauses());
        }

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void update_withAllCollisions() throws Exception {
        Person personOne = repository.create(this.personOne);
        String personOneId = personOne.getId();
        Person personTwo = repository.create(this.personTwo);
        String personTwoId = personTwo.getId();

        personOne.setEmail(EMAIL2);
        personOne.setUsername(USERNAME2);
        personOne.setDni(DNI2);
        try {
            repository.update(personOne);
            fail();
        } catch (PersonCollisionException e) {
            assertArrayEquals(new String[]{EMAIL_EXISTS, USERNAME_EXISTS, DNI_EXISTS}, e.getCauses());
        }

        repository.delete(personOneId);
        repository.delete(personTwoId);
    }

    @Test
    public void delete() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        Optional<Person> possibleDeleted = repository.delete(personId);
        assertTrue(possibleDeleted.isPresent());

        Optional<Person> possiblePerson = repository.findById(personId);
        assertFalse(possiblePerson.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void delete_null_returnsEmpty() throws Exception {
        repository.delete(null);
    }

    @Test
    public void deleteAll() throws Exception {
        Person person = repository.create(this.personOne);
        String personId = person.getId();

        repository.deleteAll();

        Optional<Person> possiblePerson = repository.findById(personId);
        assertFalse(possiblePerson.isPresent());
    }

    @Test
    public void findByCompany_companyNotExists_returnsEmpty() throws Exception {
        Company company = new Company().setId("FAKE_ID");
        List<Person> technicians = repository.findTechniciansByCompany(company);
        assertThat(technicians.size(), is(0));
    }

    @Test
    public void findByCompany_nullCompany_returnsEmpty() throws Exception {
        List<Person> technicians = repository.findTechniciansByCompany(null);
        assertThat(technicians.size(), is(0));
    }

    @Test
    public void findByCompany_noSavedCompany_returnsEmpty() throws Exception {
        List<Person> technicians = repository.findTechniciansByCompany(new Company());
        assertThat(technicians.size(), is(0));
    }

    @Test
    public void findByCompany_companyExistsWithTechnician_returnsEmpty() throws Exception {
        Company company = companyRepository.create(new Company());
        List<Person> technicians = repository.findTechniciansByCompany(company);
        assertThat(technicians.size(), is(0));
        companyRepository.delete(company.getId());
    }

    @Test
    public void findByCompany_companyExistsWithTechnicians_returnsTechnicians() throws Exception {
        Company company = companyRepository.create(new Company());

        Person technician1 = repository.create(new Person().setRole(PersonRole.TECHNICIAN));
        Person technician2 = repository.create(new Person().setRole(PersonRole.TECHNICIAN));

        Person savedTechnician1 = repository.findById(technician1.getId()).get();
        Person savedTechnician2 = repository.findById(technician2.getId()).get();

        company.addWorker(savedTechnician1);
        company.addWorker(savedTechnician2);

        companyRepository.update(company);

        List<Person> technicians = repository.findTechniciansByCompany(company);
        assertThat(technicians.size(), is(2));
        assertThat(technicians.contains(savedTechnician1), is(true));
        assertThat(technicians.contains(savedTechnician2), is(true));

        repository.deleteAll();
        companyRepository.deleteAll();
    }
}