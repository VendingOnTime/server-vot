package com.vendingontime.backend.models;

import com.vendingontime.backend.models.bodymodels.SubmitData;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * Created by miguel on 7/3/17.
 */

@Entity
@Data
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
    @Column private String password;
    @Column(unique = true) private String dni;
    @Column private String name;
    @Column private String surnames;
    @Column @Enumerated private PersonRole role;

    public Person() {
        super();
    }

    public Person(SubmitData sd) {
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
}
