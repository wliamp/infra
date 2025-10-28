package io.wliamp.gtw.config;

import io.wliamp.gtw.compo.CorsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@RefreshScope
@Slf4j
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
@RequiredArgsConstructor
public class CorsGlobalConfig implements WebFluxConfigurer {
    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] originsArray = corsProperties.getAllowedOrigins().toArray(new String[0]);
        registry.addMapping("/**")
                .allowedOrigins(originsArray)
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        log.info("CORS configuration applied. Allowed origins: {}", (Object) originsArray);
    }
}
