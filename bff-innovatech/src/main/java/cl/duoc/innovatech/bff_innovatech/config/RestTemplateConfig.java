package cl.duoc.innovatech.bff_innovatech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Factory Method pattern: centraliza la creación del RestTemplate
 * con timeouts configurables desde application.properties.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${app.rest.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${app.rest.read-timeout:10000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofMillis(connectTimeout))
                .readTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
}
