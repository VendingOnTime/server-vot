package com.vendingontime.backend.models;

import org.eclipse.persistence.annotations.UuidGenerator;

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
public abstract class AbstractEntity {
    @Id @GeneratedValue(generator = "PER_ID_GEN") public String id;

    public String getId() {
        return id;
    }

    public AbstractEntity setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEntity entity = (AbstractEntity) o;

        return (id != null ? !id.equals(entity.id) : entity.id != null);
    }
}
