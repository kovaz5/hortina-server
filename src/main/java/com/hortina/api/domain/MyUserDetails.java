package com.hortina.api.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MyUserDetails implements UserDetails {

    private final Usuario usuario;

    public MyUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // De momento no vamos a usar roles
    }

    @Override
    public String getPassword() {
        return usuario.getPassword_hash(); // Tu columna de password en la DB
    }

    @Override
    public String getUsername() {
        return usuario.getEmail(); // O el campo que uses como login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
