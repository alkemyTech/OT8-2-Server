package com.alkemy.wallet.service;

import java.util.List;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.BalanceDto;

public interface IAccountService {
    
    List<AccountDto> getAccountsByUserId(Long id);
    List<BalanceDto> getBalanceById(Long id);

}
