package com.alkemy.wallet.service;

import java.util.List;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.BalanceDto;
import com.alkemy.wallet.dto.response.PageableAccountResponseDto;

public interface IAccountService {
    PageableAccountResponseDto getAllAccounts(int page);
    List<AccountDto> getAccountsByUserId(Long id);
    List<BalanceDto> getBalanceById(Long id);

}
