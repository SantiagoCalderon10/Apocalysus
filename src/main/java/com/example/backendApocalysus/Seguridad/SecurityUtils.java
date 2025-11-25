package com.example.backendApocalysus.Seguridad;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {


    public static int getUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            System.out.println("🔍 ========== SecurityUtils.getUserId() ==========");
            System.out.println("Authentication: " + auth);

            if (auth == null) {
                System.err.println("❌ Authentication es NULL");
                return 0;
            }

            Object principal = auth.getPrincipal();
            System.out.println("Principal: " + principal);
            System.out.println("Principal type: " + principal.getClass().getName());

            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                int userId = userDetails.getId();
                System.out.println("✅ User ID extraído: " + userId);
                System.out.println("===============================================");
                return userId;
            }

            System.err.println("❌ Principal NO es UserDetailsImpl");
            System.err.println("Es: " + principal.getClass().getName());
            System.err.println("Valor: " + principal);
            System.out.println("===============================================");
            return 0;

        } catch (Exception e) {
            System.err.println("❌ EXCEPCIÓN en SecurityUtils.getUserId()");
            e.printStackTrace();
            return 0;
        }
    }
}
