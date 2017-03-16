package com.vendingontime.backend.routes.utils;

/**
 * Created by Alberto on 13/03/2017.
 */
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

    public Boolean getSuccess() {
        return success;
    }

    public RESTResult setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public Object getData() {
        return data;
    }

    public RESTResult setData(Object data) {
        this.data = data;
        return this;
    }

    public Object getError() {
        return error;
    }

    public RESTResult setError(Object error) {
        this.error = error;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RESTResult that = (RESTResult) o;

        if (success != null ? !success.equals(that.success) : that.success != null) return false;
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return error != null ? error.equals(that.error) : that.error == null;
    }

    @Override
    public int hashCode() {
        int result = success != null ? success.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RESTResult{" +
                "success=" + success +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}
