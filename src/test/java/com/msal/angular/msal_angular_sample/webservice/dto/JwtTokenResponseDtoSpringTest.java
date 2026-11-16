package com.msal.angular.msal_angular_sample.webservice.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msal.angular.msal_angular_sample.webservice.dto.response.JwtTokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtTokenResponseDtoTest {

    private JwtTokenResponseDto dto;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = """
            {
              "iss": "https://login.microsoftonline.com/common/v2.0",
              "azp": "12345678-abcd-efgh-ijkl-1234567890ab",
              "azpacr": "1",
              "name": "John Doe",
              "oid": "87654321-dcba-hgfe-lkji-ba0987654321",
              "rh": "0.AZMA...",
              "tid": "11111111-2222-3333-4444-555555555555",
              "ver": "2.0",
              "xms_ftd": "some_custom_value",
              "sub": "abcdef12345",
              "aud": "api://your-client-id",
              "iat": 1711968000,
              "exp": 1711971600,
              "email": "john.doe@example.com",
              "extra_unknown_field": "should_be_ignored"
            }
            """;
        dto = objectMapper.readValue(jsonPayload, JwtTokenResponseDto.class);
    }

    @Test
    void testDtoIsNotNull() {
        assertNotNull(dto, "The deserialized JwtTokenResponseDto instance should not be null.");
    }

    @Test
    void testIssuerMapping() {
        assertEquals("https://login.microsoftonline.com/common/v2.0", dto.getIss(), "The issuer ('iss') field was not mapped correctly.");
    }

    @Test
    void testNameMapping() {
        assertEquals("John Doe", dto.getName(), "The 'name' field was not mapped correctly.");
    }

    @Test
    void testSnakeCaseToCamelCaseMapping() {
        assertEquals("some_custom_value", dto.getXmsFtd(), "The snake_case field 'xms_ftd' was not mapped correctly to camelCase 'xmsFtd'.");
    }

    @Test
    void testIssuedAtMapping() {
        assertEquals(1711968000L, dto.getIat(), "The issued at ('iat') timestamp was not mapped correctly.");
    }

    @Test
    void testEmailMapping() {
        assertEquals("john.doe@example.com", dto.getEmail(), "The 'email' field was not mapped correctly.");
    }
}