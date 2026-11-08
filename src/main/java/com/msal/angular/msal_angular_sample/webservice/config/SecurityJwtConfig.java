package com.msal.angular.msal_angular_sample.webservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Configuration class responsible for setting up Spring Security JWT decoders.
 * This class dynamically switches token validation strategies based on the active Spring profile
 * to ensure that local testing workarounds are strictly disabled in production.
 */
@Configuration
public class SecurityJwtConfig {

    /**
     * The Microsoft Entra ID (Azure AD) issuer URI used to fetch public keys
     * for validating real OAuth2/OIDC tokens.
     */
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String azureIssuerUri;

    /**
     * Production JwtDecoder Bean.
     * <p>
     * <b>Active Profile:</b> {@code prod}
     * <p>
     * <b>Behavior:</b> Strictly enforces production security by validating only official
     * Microsoft Azure AD tokens via asymmetric RSA public/private key verification.
     * Local or custom symmetric tokens are completely rejected.
     *
     * @return A standard {@link JwtDecoder} configured via the Azure issuer URI.
     */
    @Bean
    @Profile("prod")
    public JwtDecoder productionJwtDecoder() {
        return JwtDecoders.fromIssuerLocation(azureIssuerUri);
    }

    /**
     * Development / Local JwtDecoder Bean.
     * <p>
     * <b>Active Profile:</b> Any profile except {@code prod} (e.g., default, local, dev, test).
     * <p>
     * <b>Behavior:</b> Provides a flexible composite decoder that supports <b>BOTH</b>
     * real Microsoft Azure AD tokens and local custom HS256-signed test tokens.
     * If the incoming token fails Azure validation, it attempts to validate it using the local secret key.
     *
     * @param secretKey The Base64-encoded secret key injected from properties,
     *                  with a safe default fallback string if property is missing.
     * @return A composite {@link JwtDecoder} supporting dual validation paths.
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