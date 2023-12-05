package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.entity.User;

import java.util.List;

public interface IUserService {
    List<UserDto> getUsers();
    User deleteUserById(Long id);
}
