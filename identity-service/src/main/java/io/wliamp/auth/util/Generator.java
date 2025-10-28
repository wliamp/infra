package io.wliamp.auth.util;

import static java.util.UUID.randomUUID;

public class Generator {
    public static String generateCode(int size) {
        return randomUUID()
                .toString()
                .replace("-", "")
                .replaceAll("[^A-Za-z0-9]", "")
                .substring(0, size)
                .toUpperCase();
    }
}
