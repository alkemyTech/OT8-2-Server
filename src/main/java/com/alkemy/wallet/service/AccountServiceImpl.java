package com.alkemy.wallet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.alkemy.wallet.dto.BalanceDto;

import com.alkemy.wallet.dto.request.UpdateAccountRequestDto;


import com.alkemy.wallet.dto.response.PageableAccountResponseDto;
import com.alkemy.wallet.repository.IAccountRepository;

import com.alkemy.wallet.dto.FixedTermDepositDto;
import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.entity.FixedTermDeposit;
import com.alkemy.wallet.entity.Transaction;
import com.alkemy.wallet.enums.ECurrency;
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

    @Autowired
    private IAccountRepository accountRepository;

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
    public List<BalanceDto> getBalanceById(Long id){
        Optional<User> optionalUser=userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            List<Account> accounts=user.getAccounts();
            List<BalanceDto> balancesDto= new ArrayList<>();
            for (Account account : accounts){
                BalanceDto balanceDto= new BalanceDto(
                        account.getId(),
                        account.getCurrency().name(),
                        account.getBalance(),
                        getHistory(id),
                        getFixedTerms(id)
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


    public List<TransactionDto> getHistory(Long id){
        Optional<User> optionalUser=userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            List<Account> accounts=user.getAccounts();
            List<TransactionDto> transactionsDto= new ArrayList<>();
            for(Account account:accounts){
                List<Transaction> transactions=account.getTransactions();
                for(Transaction transaction:transactions){
                    TransactionDto transactionDto=new TransactionDto(
                            account.getId(),
                            transaction.getId(),
                            transaction.getAmount(),
                            transaction.getType().name(),
                            transaction.getDescription(),
                            transaction.getTransactionDate()
                    );
                    transactionsDto.add(transactionDto);
                }
            }
            return transactionsDto;
        }
        return null;
    }
    public List<FixedTermDepositDto> getFixedTerms(Long id){
        Optional<User> optionalUser=userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            List<Account> accounts=user.getAccounts();
            List<FixedTermDepositDto> fixedTermDepositsDto= new ArrayList<>();
            for(Account account:accounts){
                List<FixedTermDeposit> fixedTermDeposits=account.getFixedTermDeposits();
                for(FixedTermDeposit fixedTermDeposit:fixedTermDeposits){
                    FixedTermDepositDto fixedTermDepositDto=new FixedTermDepositDto(
                            account.getId(),
                            fixedTermDeposit.getId(),
                            fixedTermDeposit.getAmount(),
                            fixedTermDeposit.getInterest(),
                            fixedTermDeposit.getCreationDate(),
                            fixedTermDeposit.getClosingDate()
                    );
                    fixedTermDepositsDto.add(fixedTermDepositDto);
                }
            }
            return fixedTermDepositsDto;
        }
        return null;
    }


    @Override
    public AccountDto createAccount(Long userId, ECurrency currency) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();

            Optional<Account> existAccount = accountRepository.findByUserAndCurrency(user, currency);
            if(existAccount.isPresent()){
                return null;
            }

            Account account = new Account();
            account.setUser(user);
            account.setBalance(0.0);
            account.setCurrency(currency);

            if(ECurrency.ARS.equals(currency)){
                account.setTransactionLimit(300000.0);
            } else if (ECurrency.USD.equals(currency)) {
                account.setTransactionLimit(1000.0);
            }

            accountRepository.save(account);
            AccountDto accountDto = new AccountDto(
                    account.getUser().getUsername(),
                    account.getId(),
                    account.getCurrency().name(),
                    account.getTransactionLimit(),
                    account.getBalance());
            return accountDto;
        }
        return null;
    }

}

