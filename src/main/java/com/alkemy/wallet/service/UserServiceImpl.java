package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.dto.request.UserUpdateRequestDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.repository.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService{

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(IUserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> usersDto = users.stream().map((user) -> {
            List<Account> userAccounts = user.getAccounts();
            List<AccountDto> accountsDto = userAccounts.stream().map(account -> {
                return new AccountDto(
                        user.getEmail(),
                        account.getId(),
                        account.getCurrency().name(),
                        account.getBalance(),
                        account.getTransactionLimit()
                );
            }).toList();
            return new UserDto(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole().getName().name(),
                    accountsDto
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

    @Override
    public UserInfoResponseDto updateUser(Long id, UserUpdateRequestDto userRequest, String token) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            String userEmail = jwtService.extractUserName(token.substring(7));
            if(Objects.equals(userEmail, user.getEmail())){
                boolean isUpdated = false;
                if(userRequest.getFirstName() != null && !userRequest.getFirstName().isBlank()){
                    user.setFirstName(userRequest.getFirstName());
                    isUpdated = true;
                }
                if(userRequest.getLastName() != null && !userRequest.getLastName().isBlank()){
                    user.setLastName(userRequest.getLastName());
                    isUpdated = true;
                }
                if(userRequest.getPassword() != null && !userRequest.getPassword().isBlank()){
                    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
                    isUpdated = true;
                }
                if(isUpdated){
                    userRepository.save(user);
                    return new UserInfoResponseDto(
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getUpdateDate()
                    );
                }
            }
        }
        return null;
    }
}
