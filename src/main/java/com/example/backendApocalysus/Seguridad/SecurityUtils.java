package com.example.backendApocalysus.Seguridad;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {


    public static int getUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = (UserDetailsImpl) auth.getPrincipal();
        return user.getId();
    }
}
