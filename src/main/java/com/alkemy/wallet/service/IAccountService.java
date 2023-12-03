package com.alkemy.wallet.service;

import java.util.List;
import com.alkemy.wallet.dto.AccountDto;

public interface IAccountService {
    
    List<AccountDto> getAccountsByUserId(Long id);

}
