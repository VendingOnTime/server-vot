package com.vendingontime.backend.initializers;

import javax.persistence.Persistence;
import java.util.HashMap;

/**
 * Created by miguel on 13/3/17.
 */
public class InitDB {
    public static void generateSchemas() {
        Persistence.generateSchema("dataSource", new HashMap());
    }
}
