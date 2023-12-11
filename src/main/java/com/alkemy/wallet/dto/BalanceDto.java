package com.alkemy.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BalanceDto {
    private long accountID;
    private String currency;
    private double balance;
    private List<TransactionDto> history;
    private List<FixedTermDepositDto> fixedTerm;
}
