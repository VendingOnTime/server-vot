package com.vendingontime.backend.models;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by miguel on 28/3/17.
 */
@MappedSuperclass
@UuidGenerator(name = "PER_ID_GEN")
public abstract class AbstractEntity {
    @Id @GeneratedValue(generator = "PER_ID_GEN") public String id;

    public String getId() {
        return id;
    }

    public AbstractEntity setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEntity entity = (AbstractEntity) o;

        return (id != null ? !id.equals(entity.id) : entity.id != null);
    }
}
