package com.grid.store.utilities;

import com.grid.store.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;

import java.util.Collection;

public class Validator {

    // Checks if a string is null or empty
    public static boolean isNull(String str) {
        return str == null || str.isEmpty();
    }

    // Checks if a string is null, empty, or blank (contains only whitespace)
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Checks if an object is null
    public static boolean isNull(Object object) {
        return object == null;
    }

    // Checks if a collection is null or empty
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static Long getUserId(HttpSession httpSession) {
        Long userId = (Long) httpSession.getAttribute("userId");

        if (userId == null) {
            throw new UnauthorizedException("User ID is missing in session");
        }

        return userId;
    }

}

