package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.dto.response.PageableUserResponseDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.repository.IUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public PageableUserResponseDto getUsers(int page) {
        int pageToFind = page > 0 ? page-1 : 0;
        PageRequest pr = PageRequest.of(pageToFind,10);
        Page<User> userPage = userRepository.findAll(pr);
        long count = userPage.getTotalElements();
        int pages = userPage.getTotalPages();
        String prevPage = userPage.hasPrevious() ? "/api/v1/users?page="+(page-1) : null;
        String nextPage = userPage.hasNext() ? "/api/v1/users?page="+(page+1) : null;
        if(pages < page){
            return null;
        }
        List<User> users = userPage.getContent();
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
        return new PageableUserResponseDto(
                count,
                pages,
                prevPage,
                nextPage,
                usersDto
        );
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
