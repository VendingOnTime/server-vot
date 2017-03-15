package com.vendingontime.backend.routes.utils;

import lombok.Data;

/**
 * Created by Alberto on 13/03/2017.
 */
@Data
public class Result {
    private Boolean success;
    private Object data;
    private Object error;

    public Result() {
        super();
    }

    public Result(Type type, Object obj) {
        switch (type) {
            case OK:
                initializeOk(obj);
                break;
            case ERROR:
                initializeError(obj);
                break;
        }
    }

    private void initializeOk(Object obj) {
        this.success = true;
        this.data = obj;
    }

    private void initializeError(Object obj) {
        this.success = false;
        this.error = obj;
    }

    public enum Type {
        ERROR, OK
    }
}
