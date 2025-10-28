package io.wliamp.auth.util;

import java.util.List;
import java.util.Map;
import io.wliamp.auth.entity.Aud;
import io.wliamp.auth.entity.Scope;

public class Builder {
    public static Map<String, Object> buildTokenExtraClaims(List<Scope> scopes, List<Aud> auds) {
        return Map.of(
                "scope", Parser.parseScope(scopes),
                "aud", Parser.parseAudience(auds));
    }
}
