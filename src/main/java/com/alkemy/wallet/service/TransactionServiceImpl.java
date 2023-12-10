package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.dto.request.UpdateTransactionRequestDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.Transaction;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.repository.ITransactionRepository;
import com.alkemy.wallet.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements ITransactionService {
    @Autowired
    private JwtServiceImpl jwtService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ITransactionRepository transactionRepository;
    @Override
    public List<TransactionDto> getTransactionsByUserId(Long id){
        Optional<User> optionalUser=userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();

            List<Account> accounts=user.getAccounts();
            List<TransactionDto> transactionsDto= new ArrayList<>();
            for(Account account:accounts){
                List<Transaction> transactions=account.getTransactions();
                for(Transaction transaction:transactions){
                    TransactionDto transactionDto=new TransactionDto(
                            account.getId(),
                            transaction.getId(),
                            transaction.getAmount(),
                            transaction.getType().name(),
                            transaction.getDescription(),
                            transaction.getTransactionDate()
                    );
                    transactionsDto.add(transactionDto);
                }
            }
            return transactionsDto;
        }
        return null;
    }

    @Override
    public TransactionDto updateTransactionDescription(Long id, UpdateTransactionRequestDto updateRequest, String token) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if(transactionOptional.isPresent()){
            Transaction transaction = transactionOptional.get();
            String userEmail = jwtService.extractUsername(token.substring(7));
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                if(Objects.equals(transaction.getAccount().getUser().getId(), user.getId())){
                    if(!updateRequest.getDescription().isBlank()){
                        transaction.setDescription(updateRequest.getDescription());
                        transactionRepository.save(transaction);
                        return new TransactionDto(
                                transaction.getAccount().getId(),
                                transaction.getId(),
                                transaction.getAmount(),
                                transaction.getType().name(),
                                transaction.getDescription(),
                                transaction.getTransactionDate()
                        );
                    }
                }
            }
        }
        return null;
    }

}
