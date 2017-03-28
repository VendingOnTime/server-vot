package com.vendingontime.backend.repositories;

import java.util.Optional;

/**
 * Created by miguel on 7/3/17.
 */
public interface Repository<ID, MODEL> {
    MODEL create(MODEL model);
    Optional<MODEL> findById(ID id);
    Optional<MODEL> update(MODEL model);
    Optional<MODEL> delete(ID id);
}
