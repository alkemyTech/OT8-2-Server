package com.alkemy.wallet.controller;

import java.util.List;
import com.alkemy.wallet.dto.BalanceDto;
import com.alkemy.wallet.dto.request.UpdateAccountRequestDto;
import com.alkemy.wallet.service.IAccountService;
import com.alkemy.wallet.dto.response.PageableAccountResponseDto;
import com.alkemy.wallet.enums.ECurrency;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.service.AccountServiceImpl;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    

    private final IAccountService accountService;

    public AccountController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }



    @GetMapping
    public ResponseEntity<PageableAccountResponseDto> getAllAccounts(@RequestParam(defaultValue = "0") int page){
        PageableAccountResponseDto response = accountService.getAllAccounts(page);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<List<AccountDto>> getAccountsByUserId(@PathVariable Long id) {
        List<AccountDto> accountsDto = accountService.getAccountsByUserId(id);
        return new ResponseEntity<>(accountsDto, HttpStatus.OK);
    }
    @GetMapping("/balance/{id}")
    public ResponseEntity<List<BalanceDto>>getBalanceById(@PathVariable long id){
        List<BalanceDto> balanceDto =accountService.getBalanceById(id);
        return new ResponseEntity<>(balanceDto,HttpStatus.OK);
    }


    @PatchMapping"/{id}")
    public ResponseEntity<AccountDto> updateTransactionLimit(@PathVariable Long id, @Valid @RequestBody UpdateAccountRequestDto updateRequest, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        AccountDto accountDto = accountService.updateTransactionLimit(id,updateRequest,token);
        return new ResponseEntity<>(accountDto,HttpStatus.OK);

    @PostMapping("/{userId}")
    public ResponseEntity<AccountDto> createAccount(@PathVariable Long userId, @RequestBody AccountDto account){
        AccountDto responseAccount = accountService.createAccount(userId, ECurrency.valueOf(account.getCurrency()));
        return new ResponseEntity<>(responseAccount, HttpStatus.CREATED);

    }
}