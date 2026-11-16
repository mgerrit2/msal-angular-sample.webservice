package com.msal.angular.msal_angular_sample.webservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Decoded JWT token claims")
public class JwtTokenResponseDto {

    @JsonProperty("iss")
    @Size(max = 2048)
    @Pattern(regexp = "^https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$")
    @Schema(description = "Token issuer URL", format = "uri", maxLength = 2048)
    private String iss;

    @JsonProperty("azp")
    @Size(max = 256)
    @Pattern(regexp = "^[\\w\\-.]+$")
    @Schema(description = "Authorized party (client ID)", maxLength = 256)
    private String azp;

    @JsonProperty("azpacr")
    @Size(max = 32)
    @Pattern(regexp = "^[0-9]+$")
    @Schema(description = "Authorized party authentication context class reference", maxLength = 32)
    private String azpacr;

    @JsonProperty("name")
    @Size(max = 256)
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s.'-]+$")
    @Schema(description = "Display name of the user",maxLength = 256)
    private String name;

    @JsonProperty("oid")
    @Size(max = 64)
    @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$")
    @Schema(description = "Object ID of the user in Entra ID", format = "uuid", maxLength = 64)
    private String oid;

    @JsonProperty("rh")
    @Size(max = 512)
    @Pattern(regexp = "^[\\w\\-._~+/=]+$")
    @Schema(description = "Internal Entra ID token renewal handle", maxLength = 512)
    private String rh;

    @JsonProperty("tid")
    @Size(max = 64)
    @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$")
    @Schema(description = "Tenant ID", format = "uuid", maxLength = 64)
    private String tid;

    @JsonProperty("ver")
    @Size(max = 16)
    @Pattern(regexp = "^[0-9]+\\.[0-9]+$")
    @Schema(description = "Token version", maxLength = 16)
    private String ver;

    @JsonProperty("xms_ftd")
    @Size(max = 512)
    @Pattern(regexp = "^[\\w\\-._~+/=]+$")
    @Schema(description = "Entra ID federated tenant data", maxLength = 512)
    private String xmsFtd;

    @JsonProperty("sub")
    @Size(max = 256)
    @Pattern(regexp = "^[\\w\\-._~+/=]+$")
    @Schema(description = "Subject identifier", maxLength = 256)
    private String sub;

    @JsonProperty("aud")
    @Size(max = 256)
    @Pattern(regexp = "^[\\w\\-.:/]+$")
    @Schema(description = "Intended audience", maxLength = 256)
    private String aud;

    @JsonProperty("iat")
    @Min(0)
    @Schema(description = "Issued at timestamp (Unix epoch seconds)", minimum = "0", maximum = "9999999999")
    private long iat;

    @JsonProperty("exp")
    @Min(0)
    @Schema(description = "Expiration timestamp (Unix epoch seconds)", minimum = "0", maximum = "9999999999")
    private long exp;

    @JsonProperty("email")
    @Email
    @Size(max = 320)
    @Schema(description = "User email address", format = "email", maxLength = 320)
    private String email;
}
