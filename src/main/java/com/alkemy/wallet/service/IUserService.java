package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.dto.response.UserInfoResponseDto;
import com.alkemy.wallet.entity.User;

import java.util.List;

public interface IUserService {
    List<UserDto> getUsers();
    UserInfoResponseDto deleteUserById(Long id, String token);
}
