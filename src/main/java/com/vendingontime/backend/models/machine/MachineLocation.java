package com.vendingontime.backend.models.machine;

import com.vendingontime.backend.models.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by miguel on 28/3/17.
 */

@Entity
public class MachineLocation extends AbstractEntity{
    @Column private String name;
    @OneToOne(optional = false, mappedBy = "location") private Machine machine;

    public MachineLocation() {
        super();
    }

    public void update(MachineLocation location) {
        this.name = location.getName();
        this.machine = location.getMachine();
    }

    public String getName() {
        return name;
    }

    public MachineLocation setName(String name) {
        this.name = name;
        return this;
    }

    public Machine getMachine() {
        return machine;
    }

    public MachineLocation setMachine(Machine machine) {
        this.machine = machine;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MachineLocation that = (MachineLocation) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return getMachine() != null ? getMachine().equals(that.getMachine()) : that.getMachine() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getMachine() != null ? getMachine().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MachineLocation{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", machine=" + machine +
                '}';
    }
}
