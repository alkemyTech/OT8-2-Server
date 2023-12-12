package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.dto.request.UpdateTransactionRequestDto;

import com.alkemy.wallet.dto.request.DepositRequestDto;

import com.alkemy.wallet.dto.response.TransactionResponseDto;

import java.util.List;

public interface ITransactionService {



    TransactionDto updateTransactionDescription(Long id, UpdateTransactionRequestDto updateRequest, String token);

    TransactionResponseDto getTransaction(Long id, String token);
    List<TransactionDto> getTransactionsByUserId(Long userId);
    TransactionResponseDto createDeposit(DepositRequestDto depositRequest, String token);

}
