package com.alkemy.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class AccountDto {
    private String username;
    private Long accountId;
    private String currency;
    private Double transactionLimit;
    private Double balance;
}
