package com.alkemy.wallet.controller;

import java.util.List;

import com.alkemy.wallet.dto.BalanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}