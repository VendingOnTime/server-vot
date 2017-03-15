package com.vendingontime.backend.routes.utils;

import lombok.Data;

/**
 * Created by Alberto on 13/03/2017.
 */
@Data
public class RESTResult {
    private Boolean success;
    private Object data;
    private Object error;

    public RESTResult() {
        super();
    }

    public RESTResult(Boolean success, Object content) {
        this.success = success;
        if (success) this.data = content;
        else this.error = content;
    }
}
