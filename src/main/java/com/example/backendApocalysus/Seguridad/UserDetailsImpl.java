package com.example.backendApocalysus.Seguridad;

import com.example.backendApocalysus.Entidades.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserDetailsImpl implements UserDetails {


    private int id;
    private String correo;
    private String contrasena;
    private Collection<? extends GrantedAuthority> authorities;


    public UserDetailsImpl(Usuario usuario) {
        this.id = usuario.getIdUsuario();
        this.correo = usuario.getCorreo();
        this.contrasena = usuario.getContrasena();

        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()));
    }


    public int getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
