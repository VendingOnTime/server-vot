package com.vendingontime.backend.initializers;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

/**
 * Created by miguel on 13/3/17.
 */
public class DBInitializer {

    @Inject
    public DBInitializer(PersistService service) {
        service.start();
    }
}
