package io.wliamp.auth.util;

import java.util.List;
import java.util.Map;

import io.wliamp.auth.entity.Aud;
import io.wliamp.auth.entity.Scope;

import static io.wliamp.auth.util.Parser.parseAudience;
import static io.wliamp.auth.util.Parser.parseScope;
import static java.util.Map.of;

public class Builder {
    public static Map<String, Object> buildTokenExtraClaims(List<Scope> scopes, List<Aud> auds) {
        return of(
                "scope", parseScope(scopes),
                "aud", parseAudience(auds)
        );
    }
}
