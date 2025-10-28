package io.wliamp.auth.dto;

import lombok.Builder;

@Builder
public record Tokens(String access, String refresh) {}
