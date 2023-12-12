package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.dto.request.UpdateTransactionRequestDto;
import com.alkemy.wallet.dto.request.TransactionRequestDto;
import com.alkemy.wallet.dto.response.PageableTransactionResponseDto;
import com.alkemy.wallet.dto.response.TransactionResponseDto;
import com.alkemy.wallet.dto.response.TransactionResponseDto;
import com.alkemy.wallet.dto.request.DepositRequestDto;
import com.alkemy.wallet.dto.response.TransactionResponseDto;
import java.util.List;

public interface ITransactionService {
    TransactionDto updateTransactionDescription(Long id, UpdateTransactionRequestDto updateRequest, String token);
    TransactionResponseDto getTransaction(Long id, String token);
    PageableTransactionResponseDto getTransactionsByUserId(Long userId, int page,String token);
    TransactionResponseDto createDeposit(TransactionRequestDto depositRequest, String token);
    TransactionResponseDto createPayment(TransactionRequestDto paymentRequest, String token);

}
