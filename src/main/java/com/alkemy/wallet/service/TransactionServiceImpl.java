package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.dto.request.DepositRequestDto;
import com.alkemy.wallet.dto.response.TransactionResponseDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.Transaction;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.enums.ETransactionType;
import com.alkemy.wallet.repository.IAccountRepository;
import com.alkemy.wallet.repository.ITransactionRepository;
import com.alkemy.wallet.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private IUserRepository userRepository;
    private final IJwtService jwtService;
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;

    public TransactionServiceImpl(JwtServiceImpl jwtService, ITransactionRepository transactionRepository, IAccountRepository accountRepository) {
        this.jwtService = jwtService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

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
    public TransactionResponseDto createDeposit(DepositRequestDto depositRequest, String token) {
        if(depositRequest.getAmount() < 0.00){
            return null;
        }
        String userEmail = jwtService.extractUsername(token.substring(7));
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            List<Account> userAccounts = user.getAccounts();
            Optional<Account> accountOptional = userAccounts.stream()
                    .filter(account -> account.getCurrency().name().equals(depositRequest.getCurrency()))
                    .findFirst();
            if(accountOptional.isPresent()){
                Account account = accountOptional.get();
                Transaction newTransaction = new Transaction();
                newTransaction.setAmount(depositRequest.getAmount());
                newTransaction.setType(ETransactionType.DEPOSIT);
                newTransaction.setDescription(StringUtils.hasText(depositRequest.getDescription()) ? depositRequest.getDescription() : "");
                newTransaction.setAccount(account);
                Transaction transactionCreated = transactionRepository.save(newTransaction);

                account.setBalance(account.getBalance() + depositRequest.getAmount());
                accountRepository.save(account);
                return new TransactionResponseDto(
                        user.getEmail(),
                        account.getId(),
                        transactionCreated.getId(),
                        depositRequest.getCurrency(),
                        ETransactionType.DEPOSIT.name(),
                        transactionCreated.getAmount(),
                        transactionCreated.getDescription(),
                        transactionCreated.getTransactionDate()
                );

            }
        }
        return null;
    }
}
