package com.vendingontime.backend.config.variables;

import java.util.HashMap;
import java.util.Map;

/**
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
public class MemoryServerConfig implements ServerConfig {
    private Map<ServerVariable, String> defaultVariables;
    private Map<ServerVariable, String> variables;

    public MemoryServerConfig() {
        defaultVariables = generateDefaults();
        variables = inflateEnvVariables();
    }

    @Override
    public String getString(ServerVariable variable) {
        return variables.get(variable);
    }

    @Override
    public int getInt(ServerVariable variable) {
        return Integer.parseInt(variables.get(variable));
    }

    public void setVariable(ServerVariable variable, String value) {
        variables.put(variable, value);
    }

    private Map<ServerVariable, String> generateDefaults() {
        HashMap<ServerVariable, String> defaults = new HashMap<>();

        addDefaultVariables(defaults);

        return defaults;
    }

    private void addDefaultVariables(HashMap<ServerVariable, String> defaults) {
        for (ServerVariable serverVariable : ServerVariable.values()) {
            defaults.put(serverVariable, serverVariable.getDefaultValue());
        }
    }

    private Map<ServerVariable, String> inflateEnvVariables() {
        HashMap<ServerVariable, String> variables = new HashMap<>();

        addEnvVariables(variables);

        return variables;
    }

    private void addEnvVariables(HashMap<ServerVariable, String> variables) {
        for (ServerVariable serverVariable : ServerVariable.values()) {
            String envVariable = System.getenv(serverVariable.name());
            String variable = envVariable != null ? envVariable : defaultVariables.get(serverVariable);
            variables.put(serverVariable, variable);
        }
    }
}
