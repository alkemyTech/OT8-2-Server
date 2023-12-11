package com.alkemy.wallet.service;

import java.util.List;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.BalanceDto;

import com.alkemy.wallet.dto.response.PageableAccountResponseDto;

import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.enums.ECurrency;


public interface IAccountService {
    PageableAccountResponseDto getAllAccounts(int page);
    List<AccountDto> getAccountsByUserId(Long id);
    List<BalanceDto> getBalanceById(Long id);

    AccountDto createAccount(Long userId, ECurrency currency);
}
