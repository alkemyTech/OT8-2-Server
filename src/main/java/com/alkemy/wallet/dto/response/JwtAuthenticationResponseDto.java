package com.alkemy.wallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JwtAuthenticationResponseDto {
    private String userEmail;
    private String firstName;
    private String lastName;
    private String jwt;
}
