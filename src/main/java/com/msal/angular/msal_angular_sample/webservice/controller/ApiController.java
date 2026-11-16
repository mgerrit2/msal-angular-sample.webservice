package com.msal.angular.msal_angular_sample.webservice.controller;

import com.msal.angular.msal_angular_sample.webservice.annotation.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ProblemDetail;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Operation(summary = "getData",responses = {
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
    @AdminOnly
    @GetMapping("/protected")
    public String getData(Principal principal) {

        if (principal instanceof JwtAuthenticationToken jwtAuth) {
            // Extract specific claims from the Microsoft Entra ID v2.0 token
            String name = jwtAuth.getToken().getClaimAsString("name");
            String preferredUsername = jwtAuth.getToken().getClaimAsString("preferred_username");
            String email = jwtAuth.getToken().getClaimAsString("email");
            String oid = jwtAuth.getToken().getClaimAsString("oid");

            // Fallbacks if a specific claim is missing
            if (email == null) {
                email = preferredUsername;
            }

            return String.format("Hello %s - Your email is %s - unique id id %s", name, email,oid);
        }

        return "Hello, " + principal.getName();
    }


}
