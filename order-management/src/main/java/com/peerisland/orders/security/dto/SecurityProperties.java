package com.peerisland.orders.security.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security")
public record SecurityProperties(
        boolean allowCredentials,
        List<String> allowedOrigins,
        List<String> allowedHeaders,
        List<String> exposedHeaders,
        List<String> allowedMethods,
        List<String> allowedPublicApis
) {
        public SecurityProperties {
                allowedOrigins = allowedOrigins == null ? List.of("*") : allowedOrigins;
                allowedHeaders = allowedHeaders == null ? List.of("*") : allowedHeaders;
                exposedHeaders = exposedHeaders == null ? List.of() : exposedHeaders;
                allowedMethods = allowedMethods == null ? List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") : allowedMethods;
                allowedPublicApis = allowedPublicApis == null ? List.of() : allowedPublicApis;
        }
}
