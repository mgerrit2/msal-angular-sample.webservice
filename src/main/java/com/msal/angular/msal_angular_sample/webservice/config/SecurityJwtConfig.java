package com.msal.angular.msal_angular_sample.webservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class SecurityJwtConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String azureIssuerUri;

    /**
     * Production Decoder: Strictly validates Azure AD tokens only.
     * Active when the 'prod' profile is enabled (or as the default if no fallback is allowed).
     */
    @Bean
    @Profile("prod")
    public JwtDecoder productionJwtDecoder() {
        return JwtDecoders.fromIssuerLocation(azureIssuerUri);
    }

    /**
     * Development / Local Decoder: Supports BOTH Azure AD and local custom tokens.
     * Active ONLY when 'prod' is NOT active (e.g., local development, dev, test).
     */
    @Bean
    @Profile("!prod")
    public JwtDecoder developmentJwtDecoder(
            @Value("${application.security.jwt.secret-key:VGhpcylzYVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JITac1NlJCYXNlNjRFbmNvZGVkMTIzNDU2OA==}") String secretKey) {

        JwtDecoder azureDecoder = JwtDecoders.fromIssuerLocation(azureIssuerUri);

        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");

        NimbusJwtDecoder localDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                .build();

        return token -> {
            try {
                return azureDecoder.decode(token);
            } catch (Exception e1) {
                try {
                    return localDecoder.decode(token);
                } catch (Exception e2) {
                    throw new JwtException("Token validation failed for both Azure and local custom validators.");
                }
            }
        };
    }
}