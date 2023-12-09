package com.alkemy.wallet.controller;

import java.util.List;

import com.alkemy.wallet.dto.BalanceDto;
import com.alkemy.wallet.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.service.AccountServiceImpl;

@RestController
@RequestMapping("/api/v1")
public class AccountController {
    

    private final IAccountService accountService;

    public AccountController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<List<AccountDto>> getAccountsByUserId(@PathVariable Long id) {
        List<AccountDto> accountsDto = accountService.getAccountsByUserId(id);
        return new ResponseEntity<>(accountsDto, HttpStatus.OK);
    }
    @GetMapping("/accounts/balance/{id}")
    public ResponseEntity<List<BalanceDto>>getBalanceById(@PathVariable long id){
        List<BalanceDto> balanceDto =accountService.getBalanceById(id);
        return new ResponseEntity<>(balanceDto,HttpStatus.OK);
    }

    @PatchMapping"/{id}")
    public ResponseEntity<AccountDto> updateTransactionLimit(@PathVariable Long id, @Valid @RequestBody UpdateAccountRequestDto updateRequest, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        AccountDto accountDto = accountService.updateTransactionLimit(id,updateRequest,token);
        return new ResponseEntity<>(accountDto,HttpStatus.OK);
    }
}