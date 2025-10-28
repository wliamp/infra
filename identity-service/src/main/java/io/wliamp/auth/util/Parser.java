package io.wliamp.auth.util;

import java.util.List;
import io.wliamp.auth.entity.Aud;
import io.wliamp.auth.entity.Scope;

import static java.util.stream.Collectors.joining;

public class Parser {
    public static String parseScope(List<Scope> scopes) {
        return scopes.stream()
                .map(scope -> scope.getRes() + ":" + scope.getAct())
                .collect(joining(" "));
    }

    public static List<String> parseAudience(List<Aud> auds) {
        return auds.stream().map(Aud::getCode).toList();
    }

    public static String mask(Object value) {
        if (value == null) return "NULL";
        var s = value.toString();
        return s.length() <= 8 ? "****" : s.substring(0, 4) + "..." + s.substring(s.length() - 4);
    }
}
