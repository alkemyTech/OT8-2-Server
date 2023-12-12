package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.UserDto;

import com.alkemy.wallet.dto.response.UserInfoResponseDto;


import com.alkemy.wallet.dto.request.UserUpdateRequestDto;


import com.alkemy.wallet.dto.response.UserInfoResponseDto;

import com.alkemy.wallet.dto.response.PageableUserResponseDto;


import com.alkemy.wallet.entity.User;

import java.util.List;

public interface IUserService {

    UserInfoResponseDto deleteUserById(Long id, String token);



    UserInfoResponseDto getUserById(Long id, String token);

    PageableUserResponseDto getUsers(int page);

    User deleteUserById(Long id);
    UserInfoResponseDto updateUser(Long id, UserUpdateRequestDto userRequest, String token);

}
