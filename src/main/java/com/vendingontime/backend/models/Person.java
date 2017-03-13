package com.vendingontime.backend.models;

import com.vendingontime.backend.models.viewmodels.PersonPayload;
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
        @NamedQuery(name = "Person.findById", query = "SELECT p FROM Person p WHERE p.id = :id")
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

    public Person(PersonPayload pp) {
        this.email = pp.getEmail();
        this.username = pp.getUsername();
        this.password = pp.getPassword();
        this.dni = pp.getDni();
        this.name = pp.getName();
        this.surnames = pp.getSurnames();
        this.role = pp.getRole();
    }
}
