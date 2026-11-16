package com.msal.angular.msal_angular_sample.webservice.dto;

import com.msal.angular.msal_angular_sample.webservice.controller.ApiController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetDataWithJwtTokenAndAllClaims() throws Exception {
        var jwtPostProcessor = SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt
                        .claim("name", "John Doe")
                        .claim("email", "john.doe@example.com")
                        .claim("oid", "12345-abcde")
                );

        String responseContent = mockMvc.perform(get("/api/protected").with(jwtPostProcessor))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Hello John Doe - Your email is john.doe@example.com - unique id id 12345-abcde", responseContent,
                "The endpoint should correctly format the response using name, email, and oid claims from the JWT token.");
    }

    @Test
    void testGetDataWithJwtTokenEmailFallback() throws Exception {
        var jwtPostProcessor = SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt
                        .claim("name", "Jane Doe")
                        .claim("preferred_username", "jane.fallback@example.com")
                        .claim("oid", "67890-fghij")
                );

        String responseContent = mockMvc.perform(get("/api/protected").with(jwtPostProcessor))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Hello Jane Doe - Your email is jane.fallback@example.com - unique id id 67890-fghij", responseContent,
                "The endpoint should fall back to preferred_username when the email claim is missing from the JWT token.");
    }

    @Test
    void testGetDataWithStandardPrincipal() throws Exception {
        String responseContent = mockMvc.perform(get("/api/protected")
                        .with(SecurityMockMvcRequestPostProcessors.user("TestUser")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Hello, TestUser", responseContent,
                "The endpoint should handle non-JWT principals correctly and return a simple greeting.");
    }
}