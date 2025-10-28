package io.wliamp.auth.util;

import java.util.List;
import java.util.stream.Collectors;
import io.wliamp.auth.entity.Aud;
import io.wliamp.auth.entity.Scope;

public class Parser {
    public static String parseScope(List<Scope> scopes) {
        return scopes.stream()
                .map(scope -> scope.getRes() + ":" + scope.getAct())
                .collect(Collectors.joining(" "));
    }

    public static List<String> parseAudience(List<Aud> auds) {
        return auds.stream().map(Aud::getCode).toList();
    }

    public static String mask(Object value) {
        if (value == null) return "NULL";
        String s = value.toString();
        if (s.length() <= 8) return "****";
        return s.substring(0, 4) + "..." + s.substring(s.length() - 4);
    }
}
