package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.dto.request.DepositRequestDto;
import com.alkemy.wallet.dto.response.TransactionResponseDto;

import java.util.List;

public interface ITransactionService {
    List<TransactionDto> getTransactionsByUserId(Long id);
    TransactionResponseDto createDeposit(DepositRequestDto depositRequest, String token);
}
