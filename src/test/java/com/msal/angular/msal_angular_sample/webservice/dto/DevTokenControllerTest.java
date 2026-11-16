package com.msal.angular.msal_angular_sample.webservice.dto;

import com.msal.angular.msal_angular_sample.webservice.controller.DevTokenController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DevTokenController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables Spring Security filters so 401 doesn't block requests
@ActiveProfiles("dev")
class DevTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testReadJwtAsStringSuccessfullyDecodesPayload() throws Exception {
        // Given a mock JWT payload containing JSON data matching JwtTokenResponseDto
        String jsonPayload = "{\"iss\":\"https://login.microsoftonline.com/common/v2.0\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"iat\":1711968000,\"exp\":1711971600}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonPayload.getBytes());
        String mockJwt = "header." + encodedPayload + ".signature";
        String authHeader = "Bearer " + mockJwt;

        // When & Then
        String responseContent = mockMvc.perform(get("/api/raw-token")
                        .param("Authorization", authHeader))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        boolean containsName = responseContent.contains("John Doe");
        assertTrue(containsName, "The response JSON should successfully map and contain the name claim from the decoded JWT payload.");
    }

    @Test
    void testReadJwtAsStringHandlesInvalidTokenStructure() throws Exception {
        // Given an invalid JWT token missing the standard dot-separated structure
        String invalidAuthHeader = "Bearer invalid-token-without-chunks";

        // When & Then
        String responseContent = mockMvc.perform(get("/api/raw-token")
                        .param("Authorization", invalidAuthHeader))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        boolean containsError = responseContent.contains("Failed to decode token into DTO");
        assertTrue(containsError, "The response should contain an error message when the JWT token structure is invalid.");
    }
}