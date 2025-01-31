package com.example.librarymanager.util;

import com.example.librarymanager.security.CustomUserDetails;

import java.util.Arrays;

public class SecurityUtils {

    public static boolean hasRequiredRole(CustomUserDetails userDetails, String[] requiredRoles) {
        if (userDetails == null) {
            return false;
        }
        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        Arrays.stream(requiredRoles)
                                .anyMatch(role -> grantedAuthority.getAuthority().equals(role))
                );
    }

}
