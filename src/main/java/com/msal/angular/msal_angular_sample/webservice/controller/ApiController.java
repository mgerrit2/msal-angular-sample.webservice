package com.msal.angular.msal_angular_sample.webservice.controller;

import com.msal.angular.msal_angular_sample.webservice.annotation.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
            @ApiResponse(responseCode = "400",
                    description = "Bad Request, missing required fields."
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(responseCode = "404",
                    description = "Not Found"
            ),
            @ApiResponse(responseCode = "406",
                    description = "Not Acceptable"
            ),
            @ApiResponse(responseCode = "415",
                    description = "Bad Request, missing required fields."
            ),
            @ApiResponse(responseCode = "429",
                    description = "Retry After Some Time"
            ),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error"
            ),
            @ApiResponse(
                    responseCode = "default"
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
