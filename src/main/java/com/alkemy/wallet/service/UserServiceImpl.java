package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService{

    private final IUserRepository userRepository;

    public UserServiceImpl(IUserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> usersDto = users.stream().map((user) -> {
            return new UserDto(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole().getName().name(),
                    null
            );
        }).toList();
        return usersDto;
    }
    @Override
    public User deleteUserById(Long id) {
        Optional<User> userOptional =userRepository.findById(id);
        if(userOptional.isPresent()){
            User user=userOptional.get();
            user.setSoftDelete(Boolean.TRUE);
            userRepository.save(user);
            return user;
        }
        return null;
    }
}
