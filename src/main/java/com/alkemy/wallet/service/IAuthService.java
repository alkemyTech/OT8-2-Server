package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.request.RegisterRequestDto;
import com.alkemy.wallet.dto.response.JwtAuthenticationResponseDto;

public interface IAuthService {
    public JwtAuthenticationResponseDto registerUser(RegisterRequestDto registerRequest);
}
