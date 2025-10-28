package io.wliamp.gtw.compo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@RefreshScope
@Data
@ConfigurationProperties(prefix = "cors")
@Component
public class CorsProperties {
    private List<String> allowedOrigins;
}
