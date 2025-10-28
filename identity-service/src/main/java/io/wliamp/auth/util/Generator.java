package io.wliamp.auth.util;

import java.util.UUID;

public class Generator {
    public static String generateCode(int size) {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .replaceAll("[^A-Za-z0-9]", "")
                .substring(0, size)
                .toUpperCase();
    }
}
