package com.alkemy.wallet.controller;

import java.util.List;

import com.alkemy.wallet.dto.BalanceDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.enums.ECurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.service.AccountServiceImpl;

@RestController
@RequestMapping("/api/v1")
public class AccountController {
    
    @Autowired
    private AccountServiceImpl accountServiceImpl;

    @GetMapping("/accounts/{id}")
    public ResponseEntity<List<AccountDto>> getAccountsByUserId(@PathVariable Long id) {
        List<AccountDto> accountsDto = accountServiceImpl.getAccountsByUserId(id);
        return new ResponseEntity<>(accountsDto, HttpStatus.OK);
    }
    @GetMapping("/accounts/balance/{id}")
    public ResponseEntity<List<BalanceDto>>getBalanceById(@PathVariable long id){
        List<BalanceDto> balanceDto =accountServiceImpl.getBalanceById(id);
        return new ResponseEntity<>(balanceDto,HttpStatus.OK);
    }
    @PostMapping("/accounts/{userId}")
    public ResponseEntity<AccountDto> createAccount(@PathVariable Long userId, @RequestBody AccountDto account){
        AccountDto responseAccount = accountServiceImpl.createAccount(userId, ECurrency.valueOf(account.getCurrency()));
        return new ResponseEntity<>(responseAccount, HttpStatus.CREATED);
    }
}