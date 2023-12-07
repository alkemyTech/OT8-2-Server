package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;

import java.util.List;

public interface ITransactionService {
    List<TransactionDto> getTransactionsByUserId(Long id);
}
