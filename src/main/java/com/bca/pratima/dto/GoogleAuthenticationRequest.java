package com.bca.pratima.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleAuthenticationRequest {
    @NotBlank
    private String credential;
}
