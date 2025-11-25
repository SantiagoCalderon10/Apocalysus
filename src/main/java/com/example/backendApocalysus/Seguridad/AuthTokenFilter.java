package com.example.backendApocalysus.Seguridad;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🔍 ========== FILTRO JWT ==========");
        System.out.println("📍 URL: " + request.getMethod() + " " + request.getRequestURI());

        try {
            String token = parseJwt(request);
            System.out.println("🔑 Token extraído: " + (token != null ? "SÍ" : "NO"));

            if (token != null && jwtUtils.validateJwtToken(token)) {
                System.out.println("✅ Token válido");

                String username = jwtUtils.getUserNameFromJwtToken(token);
                System.out.println("👤 Username del token: " + username);

                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
                System.out.println("👤 UserDetails cargado - ID: " + userDetails.getId());
                System.out.println("🔐 Authorities: " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("✅ Authentication guardado correctamente");
                System.out.println("✅ Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getName());
            } else {
                System.out.println("⚠️ Token inválido o no presente");
            }
        } catch (Exception e) {
            System.err.println("❌ Error en AuthTokenFilter: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=================================");
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }

}
