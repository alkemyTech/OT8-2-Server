package com.alkemy.wallet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.alkemy.wallet.dto.BalanceDto;
import com.alkemy.wallet.dto.response.PageableAccountResponseDto;
import com.alkemy.wallet.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.repository.IUserRepository;

@Service
public class AccountServiceImpl implements IAccountService {


    private final IUserRepository userRepository;
    private final IAccountRepository accountRepository;

    public AccountServiceImpl(IUserRepository userRepository, IAccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public PageableAccountResponseDto getAllAccounts(int page) {
        int pageToFind = page > 0 ? page-1 : 0;
        PageRequest pr = PageRequest.of(pageToFind,10);
        Page<Account> accountPage = accountRepository.findAll(pr);
        long count = accountPage.getTotalElements();
        int pages = accountPage.getTotalPages();
        String prevPage = accountPage.hasPrevious() ? "/api/v1/users?page="+(page-1) : null;
        String nextPage = accountPage.hasNext() ? "/api/v1/users?page="+(page+1) : null;
        if(pages < page){
            return null;
        }
        List<Account> accounts = accountPage.getContent();
        List<AccountDto> accountsDto = accounts.stream().map(account -> {
            return new AccountDto(
                    account.getUser().getEmail(),
                    account.getId(),
                    account.getCurrency().name(),
                    account.getBalance(),
                    account.getTransactionLimit()
            );
        }).toList();
        return new PageableAccountResponseDto(
                count,
                pages,
                prevPage,
                nextPage,
                accountsDto
        );
    }

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
    
}

