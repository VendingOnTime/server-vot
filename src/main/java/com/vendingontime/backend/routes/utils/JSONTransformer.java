package com.vendingontime.backend.routes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ResponseTransformer;

/**
 * Created by Alberto on 13/03/2017.
 */
public class JSONTransformer implements ResponseTransformer {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String render(Object model) throws Exception {
        return mapper.writeValueAsString(model);
    }
}
