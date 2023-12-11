package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.Transaction;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.enums.EType;
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

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private IJwtService jwtService;

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
    public List<TransactionDto> sendUsd(Long userDestinationId, TransactionDto transactionDto, String token) {
        List<TransactionDto> responseList = new ArrayList<>();

        if (transactionDto.getAmount() < 0.00){
            return null;
        }

        String userEmail = jwtService.extractUsername(token.substring(7));
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Account> userAccounts = user.getAccounts();
            Optional<Account> optionalAccount = userAccounts.stream()
                    .filter(account -> account.getCurrency().name().equals("USD"))
                    .findFirst();

            if (optionalAccount.isPresent()){
                Account account = optionalAccount.get();
                if(account.getBalance() > transactionDto.getAmount() && transactionDto.getAmount() < account.getTransactionLimit()){
                    Transaction transactionPayment = new Transaction();
                    transactionPayment.setAmount(transactionDto.getAmount());
                    transactionPayment.setType(EType.PAYMENT);
                    transactionPayment.setDescription(StringUtils.hasText(transactionDto.getDescription()) ? transactionDto.getDescription() : "");
                    transactionPayment.setAccount(account);
                    transactionRepository.save(transactionPayment);

                    responseList.add(new TransactionDto(
                            account.getId(),
                            transactionPayment.getId(),
                            transactionPayment.getAmount(),
                            transactionPayment.getType().name(),
                            transactionPayment.getDescription(),
                            transactionPayment.getTransactionDate()
                    ));
                }
            }
        }

        TransactionDto transactionIncome = createIncomeUsd(userDestinationId, transactionDto);
        responseList.add(transactionIncome);
        return responseList;
    }

    @Override
    public TransactionDto createIncomeUsd(Long userId, TransactionDto transactionDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            List<Account> userAccounts = user.getAccounts();
            Optional<Account> optionalAccount = userAccounts.stream()
                    .filter(account -> account.getCurrency().name().equals("USD"))
                    .findFirst();

            if(optionalAccount.isPresent()){
                Account account = optionalAccount.get();

                Transaction transactionIncome = new Transaction();
                transactionIncome.setAmount(transactionDto.getAmount());
                transactionIncome.setType(EType.INCOME);
                transactionIncome.setDescription(StringUtils.hasText(transactionDto.getDescription()) ? transactionDto.getDescription() : "");
                transactionIncome.setAccount(account);
                transactionRepository.save(transactionIncome);

                return new TransactionDto(
                        account.getId(),
                        transactionIncome.getId(),
                        transactionIncome.getAmount(),
                        transactionIncome.getType().name(),
                        transactionIncome.getDescription(),
                        transactionIncome.getTransactionDate()
                );
            }
        }
        return null;
    }
}
