package com.epam.esm.dto;

public enum Permission {
    TAGS_READ("tags:read"),
    TAGS_WRITE("tags:write"),
    TAGS_DELETE("tags:delete"),
    CERTIFICATES_READ("certificates:read"),
    CERTIFICATES_WRITE("certificates:write"),
    CERTIFICATES_EDIT("certificates:edit"),
    CERTIFICATES_DELETE("certificates:delete"),
    USERS_READ("users:read"),
    ORDERS_READ("orders:read"),
    ORDERS_WRITE("orders:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
