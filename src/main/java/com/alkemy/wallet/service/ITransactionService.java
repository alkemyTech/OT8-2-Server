package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;

import java.util.List;

public interface ITransactionService {
    List<TransactionDto> getTransactionsByUserId(Long id);

    List<TransactionDto> sendArs(Long userDestinationId ,TransactionDto transactionDto, String token);
    TransactionDto createIncome(Long userId, TransactionDto transactionDto);
}
