package com.epam.esm.dto;

import org.apache.commons.collections4.SetUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {
    USER(SetUtils.hashSet(Permission.TAGS_READ, Permission.CERTIFICATES_READ, Permission.USERS_READ,
            Permission.ORDERS_READ, Permission.ORDERS_WRITE)),
    ADMIN(SetUtils.hashSet(Permission.values()));

    private final Set<Permission> permissions;

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toSet());
    }
}
