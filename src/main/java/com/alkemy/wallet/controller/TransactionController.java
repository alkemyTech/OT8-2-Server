package com.alkemy.wallet.controller;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.service.ITransactionService;
import com.alkemy.wallet.service.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/transactions")
public class TransactionController {
    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @GetMapping("/{id}")
    public ResponseEntity<List<TransactionDto>>getTransactionsByUserId(@PathVariable Long id){
        List<TransactionDto> transactionsDto=transactionServiceImpl.getTransactionsByUserId(id);
        return new ResponseEntity<>(transactionsDto, HttpStatus.OK);
    }

    @PostMapping("/sendUsd/{userId}")
    public ResponseEntity<List<TransactionDto>> sendUsd(@PathVariable Long userId, @RequestBody TransactionDto transactionDto, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        List<TransactionDto> transactionResponse = transactionServiceImpl.sendUsd(userId, transactionDto, token);
        return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
    }

}
