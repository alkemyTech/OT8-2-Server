package com.alkemy.wallet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.alkemy.wallet.dto.BalanceDto;
import com.alkemy.wallet.dto.request.UpdateAccountRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.repository.IUserRepository;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public List<AccountDto> getAccountsByUserId(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            List<Account> accounts = optionalUser.get().getAccounts();
            List<AccountDto> accountsDto = new ArrayList<>();
            for (Account account : accounts) {
                AccountDto accountDto = new AccountDto(
                    optionalUser.get().getEmail(),
                    account.getId(), 
                    account.getCurrency().name(), 
                    account.getTransactionLimit(),
                    account.getBalance()
                );
                accountsDto.add(accountDto);
            }
            return accountsDto;
        }
        return null;
    }
    @Override
    public List<BalanceDto> getBalanceById(Long Id){
        Optional<User> optionalUser=userRepository.findById(Id);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            List<Account> accounts=user.getAccounts();
            List<BalanceDto> balancesDto= new ArrayList<>();
            for (Account account : accounts){
                BalanceDto balanceDto= new BalanceDto(
                        account.getId(),
                        account.getCurrency().name(),
                        account.getBalance(),
                        null,
                        null
                );
                balancesDto.add(balanceDto);
            }
            return balancesDto;
        }
        return null;
    }

    @Override
    public AccountDto updateTransactionLimit(Long id, UpdateAccountRequestDto updateRequest, String token) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if(accountOptional.isPresent()){
            Account account = accountOptional.get();
            String userEmail = jwtService.extractUserName(token.substring(7));
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                if(Objects.equals(account.getUser().getId(), user.getId())){
                    if(updateRequest.getNewTransactionLimit() > 0.0){
                        account.setTransactionLimit(updateRequest.getNewTransactionLimit());
                        accountRepository.save(account);
                        return new AccountDto(
                                userEmail,
                                account.getId(),
                                account.getCurrency().name(),
                                account.getBalance(),
                                account.getTransactionLimit()
                        );
                    }
                }
            }
        }
        return null;
    }

}

