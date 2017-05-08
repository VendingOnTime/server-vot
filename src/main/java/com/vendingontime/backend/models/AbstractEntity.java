package com.vendingontime.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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
@MappedSuperclass
@UuidGenerator(name = "PER_ID_GEN")
public abstract class AbstractEntity<T extends AbstractEntity> {
    @Id @GeneratedValue(generator = "PER_ID_GEN") private String id;
    @Column @JsonIgnore private boolean disabled;

    public String getId() {
        return id;
    }

    public T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public T setDisabled(boolean disabled) {
        this.disabled = disabled;
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;

        AbstractEntity<?> that = (AbstractEntity<?>) o;

        if (isDisabled() != that.isDisabled()) return false;
        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (isDisabled() ? 1 : 0);
        return result;
    }

    public abstract void updateWith(T entity);

    @Override
    public String toString() {
        return "AbstractEntity{" +
                "id='" + id + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
