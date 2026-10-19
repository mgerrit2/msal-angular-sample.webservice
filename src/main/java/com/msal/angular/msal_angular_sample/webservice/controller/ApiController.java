package com.msal.angular.msal_angular_sample.webservice.controller;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiController {

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
