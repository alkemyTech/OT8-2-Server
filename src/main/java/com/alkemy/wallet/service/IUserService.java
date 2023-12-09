package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.dto.request.UserUpdateRequestDto;
import com.alkemy.wallet.entity.User;

import java.util.List;

public interface IUserService {
    List<UserDto> getUsers();
    User deleteUserById(Long id);
    UserInfoResponseDto updateUser(Long id, UserUpdateRequestDto userRequest, String token);
}
