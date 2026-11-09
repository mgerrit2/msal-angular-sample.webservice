package com.msal.angular.msal_angular_sample.webservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JwtTokenResponseDto {
    @JsonProperty("iss")
    private String iss;

    @JsonProperty("azp")
    private String azp;

    @JsonProperty("azpacr")
    private String azpacr;

    @JsonProperty("name")
    private String name;

    @JsonProperty("oid")
    private String oid;

    @JsonProperty("rh")
    private String rh;

    @JsonProperty("tid")
    private String tid;

    @JsonProperty("ver")
    private String ver;

    @JsonProperty("xms_ftd")
    private String xmsFtd;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("aud")
    private String aud;

    @JsonProperty("iat")
    private long iat;

    @JsonProperty("exp")
    private long exp;
}
