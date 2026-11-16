package com.msal.angular.msal_angular_sample.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msal.angular.msal_angular_sample.webservice.dto.response.JwtTokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@Profile("!prod") // This entire controller is disabled in production
@RestController("/api")
public class DevTokenController {

    private static final int MIN_JWT_CHUNKS = 2;

    @Operation(summary = "Get public the jwt string information",responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "4XX", // Catches 400, 401, 403, 404, etc.
                    description = "Client Error (e.g., Bad Request, Unauthorized, Not Found, Validation)",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "default",
                    description = "Unexpected Error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @Profile("!prod")
    @GetMapping("/raw-token")
    public JwtTokenResponseDto readJwtAsString(@RequestParam("Authorization") String authorizationHeader) {

        String jwtToken = authorizationHeader.substring(7);

        try {
            // JWT format: header.payload.signature
            String[] chunks = jwtToken.split("\\.");
            if (chunks.length < MIN_JWT_CHUNKS) {
                throw new IllegalArgumentException("Invalid JWT token structure.");
            }

            // Decode the payload (second part)
            Base64.Decoder decoder = Base64.getUrlDecoder();
            byte[] decodedPayloadBytes = decoder.decode(chunks[1]);

            // Map JSON payload directly to your Response DTO
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(decodedPayloadBytes, JwtTokenResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode token into DTO: " + e.getMessage(), e);
        }
    }

}
