package com.UNN.xchange.Controller.Constants;

public enum Roles {

    ROLE_BUYER("ROLE_BUYER"),
    ROLE_SELLER("ROLE_SELLER");
    
    private final String role;

    Roles(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }
}
