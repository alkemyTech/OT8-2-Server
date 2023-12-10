package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.dto.response.UserInfoResponseDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService{

    private final IUserRepository userRepository;
    private final IJwtService jwtService;
    public UserServiceImpl(IUserRepository userRepository,JwtServiceImpl jwtService){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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
    public UserInfoResponseDto deleteUserById(Long id,String token) {
        Optional<User> userOptional =userRepository.findById(id);
        if(userOptional.isPresent()){
            User user=userOptional.get();
            String userEmail=jwtService.extractUsername(token.substring(7));
            if(Objects.equals(user.getEmail(), userEmail)){
                user.setSoftDelete(Boolean.TRUE);
                userRepository.save(user);
                return new UserInfoResponseDto(
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getCreationDate(),
                        user.getUpdateDate()
                );
            }

        }
        return null;
    }
}
