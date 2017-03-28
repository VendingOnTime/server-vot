package com.vendingontime.backend.services.utils;

import com.vendingontime.backend.models.bodymodels.LogInData;

/**
 * Created by miguel on 28/3/17.
 */
public interface TokenGenerator {
    String generate(LogInData userData);
}
