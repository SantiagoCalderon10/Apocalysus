package com.example.backendApocalysus.Dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthResponseDTO {


    private String token;
    private String type = "Bearer";
    private int id;
    private String email;
    private Collection<? extends GrantedAuthority> roles;

    public AuthResponseDTO(String token, int id, String email,
                           Collection<? extends GrantedAuthority> roles) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.roles = roles;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<? extends GrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(Collection<? extends GrantedAuthority> roles) {
        this.roles = roles;
    }
}
