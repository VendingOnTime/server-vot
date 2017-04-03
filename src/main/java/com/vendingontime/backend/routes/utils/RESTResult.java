package com.vendingontime.backend.routes.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
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
