package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.UserDto;

import java.util.List;

public interface IUserService {
    List<UserDto> getUsers();
}
