package com.monolitomicroservice.teste.common.loginmodule;

import java.io.Serializable;
import java.security.Principal;

public class CustomPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    public CustomPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "CustomPrincipal{" +
                "name='" + name + '\'' +
                '}';
    }
}
