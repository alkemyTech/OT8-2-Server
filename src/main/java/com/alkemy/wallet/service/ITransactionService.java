package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.dto.request.UpdateTransactionRequestDto;

import java.util.List;

public interface ITransactionService {
    List<TransactionDto> getTransactionsByUserId(Long id);

    TransactionDto updateTransactionDescription(Long id, UpdateTransactionRequestDto updateRequest, String token);
}
