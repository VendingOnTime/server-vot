package com.vendingontime.backend.repositories;

import com.vendingontime.backend.models.Person;

import java.util.Optional;

/**
 * Created by miguel on 7/3/17.
 */
public interface CRUDRepository<ID, MODEL> {
    MODEL create(MODEL model);
    Optional<MODEL> retrieve(ID id);
    Optional<MODEL> update(MODEL model);
    Optional<MODEL> delete(ID id);
}
