package com.vendingontime.backend.config.variables;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguel on 13/3/17.
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

    private Map<ServerVariable, String> generateDefaults() {
        HashMap<ServerVariable, String> defaults = new HashMap<>();

        addDefaultVariables(defaults);

        return defaults;
    }

    /*
    * Add other default variables here
    */

    private void addDefaultVariables(HashMap<ServerVariable, String> defaults) {
        defaults.put(ServerVariable.PORT, "8080");
        defaults.put(ServerVariable.ENV, Environment.DEVELOPMENT.toString());
    }
}
