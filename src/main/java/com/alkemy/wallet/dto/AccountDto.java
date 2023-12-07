package com.alkemy.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class AccountDto {
    private String username;
    private long accountId;
    private String currency;
    private double transactionLimit;
    private double balance;
}
