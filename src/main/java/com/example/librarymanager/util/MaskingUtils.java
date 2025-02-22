package com.example.librarymanager.util;

import java.text.Normalizer;

public class MaskingUtils {

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return email;
        }
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        return localPart.charAt(0) + "*****" + localPart.charAt(localPart.length() - 1) + domainPart;
    }

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) {
            return phoneNumber;
        }
        return phoneNumber.substring(0, 2) + "*****" + phoneNumber.substring(phoneNumber.length() - 3);
    }

    public static String toSlug(String input) {
        String normalized = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        String lowerCase = withoutDiacritics.toLowerCase();

        String slug = lowerCase.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
        slug = slug.replaceAll("-+", "-").replaceAll("^-|-$", "");
        return slug;
    }

}
