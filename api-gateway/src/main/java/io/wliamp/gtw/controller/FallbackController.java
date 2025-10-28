package io.wliamp.gtw.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/{service}")
    public ResponseEntity<String> fallback(@PathVariable String service) {
        return ResponseEntity.status(503).body("Service " + service.toUpperCase() + " is unavailable");
    }
}

