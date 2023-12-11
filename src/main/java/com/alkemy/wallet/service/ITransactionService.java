package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;

import com.alkemy.wallet.dto.request.DepositRequestDto;

import com.alkemy.wallet.dto.response.TransactionResponseDto;

import java.util.List;

public interface ITransactionService {
    TransactionResponseDto getTransaction(Long id, String token);
    List<TransactionDto> getTransactionsByUserId(Long userId);
    TransactionResponseDto createDeposit(DepositRequestDto depositRequest, String token);
}
