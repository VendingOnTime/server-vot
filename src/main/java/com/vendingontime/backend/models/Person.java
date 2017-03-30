package com.vendingontime.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * Created by miguel on 7/3/17.
 */

@Entity
@UuidGenerator(name = "PER_ID_GEN")
@NamedQueries({
        @NamedQuery(name = "Person.findById", query = "SELECT p FROM Person p WHERE p.id = :id"),
        @NamedQuery(name = "Person.findByEmail", query = "SELECT p FROM Person p WHERE p.email = :email"),
        @NamedQuery(name = "Person.findByUsername", query = "SELECT p FROM Person p WHERE p.username = :username"),
        @NamedQuery(name = "Person.findByDni", query = "SELECT p FROM Person p WHERE p.dni = :dni")
})
public class Person {
    @Id @GeneratedValue(generator = "PER_ID_GEN") private String id;
    @Column(unique = true) private String email;
    @Column(unique = true) private String username;
    @Column @JsonIgnore private String password;
    @Column(unique = true) private String dni;
    @Column private String name;
    @Column private String surnames;
    @Column @Enumerated private PersonRole role;

    public Person() {
        super();
    }

    public Person(SignUpData sd) {
        this.email = sd.getEmail();
        this.username = sd.getUsername();
        this.password = sd.getPassword();
        this.dni = sd.getDni();
        this.name = sd.getName();
        this.surnames = sd.getSurnames();
        this.role = sd.getRole();
    }

    public void update(Person p) {
        this.email = p.getEmail();
        this.username = p.getUsername();
        this.password = p.getPassword();
        this.dni = p.getDni();
        this.name = p.getName();
        this.surnames = p.getSurnames();
        this.role = p.getRole();
    }

    public String getId() {
        return id;
    }

    public Person setId(String id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Person setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Person setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Person setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDni() {
        return dni;
    }

    public Person setDni(String dni) {
        this.dni = dni;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurnames() {
        return surnames;
    }

    public Person setSurnames(String surnames) {
        this.surnames = surnames;
        return this;
    }

    public PersonRole getRole() {
        return role;
    }

    public Person setRole(PersonRole role) {
        this.role = role;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        if (email != null ? !email.equals(person.email) : person.email != null) return false;
        if (username != null ? !username.equals(person.username) : person.username != null) return false;
        if (password != null ? !password.equals(person.password) : person.password != null) return false;
        if (dni != null ? !dni.equals(person.dni) : person.dni != null) return false;
        if (name != null ? !name.equals(person.name) : person.name != null) return false;
        if (surnames != null ? !surnames.equals(person.surnames) : person.surnames != null) return false;
        return role == person.role;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (dni != null ? dni.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surnames != null ? surnames.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", surnames='" + surnames + '\'' +
                ", role=" + role +
                '}';
    }
}
