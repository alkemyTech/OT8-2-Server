package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDto;
import com.alkemy.wallet.dto.request.SendTransactionRequestDto;
import com.alkemy.wallet.dto.request.TransactionRequestDto;
import com.alkemy.wallet.dto.request.UpdateTransactionRequestDto;
import com.alkemy.wallet.dto.response.PageableTransactionResponseDto;
import com.alkemy.wallet.dto.response.SendTransactionResponseDto;
import com.alkemy.wallet.dto.response.TransactionResponseDto;
import com.alkemy.wallet.entity.Account;
import com.alkemy.wallet.entity.Transaction;
import com.alkemy.wallet.entity.User;
import com.alkemy.wallet.enums.ECurrency;
import com.alkemy.wallet.enums.ERole;
import com.alkemy.wallet.enums.ETransactionType;
import com.alkemy.wallet.repository.IAccountRepository;
import com.alkemy.wallet.repository.ITransactionRepository;
import com.alkemy.wallet.repository.IUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements ITransactionService {
    private final IUserRepository userRepository;
    private final ITransactionRepository transactionRepository;
    private final IJwtService jwtService;
    private final IAccountRepository accountRepository;

    public TransactionServiceImpl(IUserRepository userRepository,ITransactionRepository transactionRepository,IAccountRepository accountRepository, JwtServiceImpl jwtService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }

    @Override
    public TransactionResponseDto getTransaction(Long id, String token) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            User transactionOwner = transaction.getAccount().getUser();
            String userEmail = jwtService.extractUsername(token.substring(7));
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getRole().getName() == ERole.ADMIN || Objects.equals(transactionOwner.getId(), user.getId())) {
                    return new TransactionResponseDto(
                            transactionOwner.getEmail(),
                            transaction.getAccount().getId(),
                            transaction.getId(),
                            transaction.getAccount().getCurrency().name(),
                            transaction.getType().name(),
                            transaction.getAmount(),
                            transaction.getDescription(),
                            transaction.getTransactionDate()
                    );
                }
            }
        }
        return null;
    }

    @Override
    public PageableTransactionResponseDto getTransactionsByUserId(Long userId, int page,String token){
        Optional<User> optionalUser=userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            String userEmail = jwtService.extractUsername(token.substring(7));
            if(Objects.equals(user.getEmail(), userEmail) || user.getRole().getName() == ERole.ADMIN){
                List<Account> accounts = user.getAccounts();
                List<TransactionDto> transactionsDto = new ArrayList<>();
                int pageToFind = page > 0 ? page-1 : 0;
                PageRequest pr = PageRequest.of(pageToFind,10, Sort.by(Sort.Direction.DESC,"transactionDate"));
                Page<Transaction> transactionPage = transactionRepository.findAllByAccountIn(accounts,pr);
                long count = transactionPage.getTotalElements();
                int pages = transactionPage.getTotalPages();
                String prevPage = transactionPage.hasPrevious() ? "/api/v1/transactions/"+userId+"?page="+(page-1) : null;
                String nextPage = transactionPage.hasNext() ? "/api/v1/transactions/"+userId+"?page="+(page+1) : null;
                if(pages < page){
                    return null;
                }
                List<Transaction> transactions = transactionPage.getContent();
                for(Transaction transaction:transactions){
                    TransactionDto transactionDto=new TransactionDto(
                            transaction.getAccount().getId(),
                            transaction.getAccount().getCurrency().name(),
                            transaction.getId(),
                            transaction.getAmount(),
                            transaction.getType().name(),
                            transaction.getDescription(),
                            transaction.getTransactionDate()
                    );
                    transactionsDto.add(transactionDto);
                }
                return new PageableTransactionResponseDto(
                        count,
                        pages,
                        prevPage,
                        nextPage,
                        transactionsDto
                );
            }
        }
        return null;
    }

    @Override
    public TransactionDto updateTransactionDescription(Long id, UpdateTransactionRequestDto updateRequest, String token) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            String userEmail = jwtService.extractUsername(token.substring(7));
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (Objects.equals(transaction.getAccount().getUser().getId(), user.getId())) {
                    if (!updateRequest.getDescription().isBlank()) {
                        transaction.setDescription(updateRequest.getDescription());
                        transactionRepository.save(transaction);
                        return new TransactionDto(
                                transaction.getAccount().getId(),
                                transaction.getAccount().getCurrency().name(),
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

    public TransactionResponseDto createDeposit(TransactionRequestDto depositRequest, String token) {
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

    @Override
    public TransactionResponseDto createPayment(TransactionRequestDto paymentRequest, String token) {
        if (paymentRequest.getAmount() < 0.00) {
            return null;
        }
        String userEmail = jwtService.extractUsername(token.substring(7));
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Account> userAccounts = user.getAccounts();
            Optional<Account> accountOptional = userAccounts.stream()
                    .filter(account -> account.getCurrency().name().equals(paymentRequest.getCurrency()))
                    .findFirst();
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                if (account.getBalance() >= paymentRequest.getAmount()) {
                    Transaction newTransaction = new Transaction();
                    newTransaction.setAmount(paymentRequest.getAmount());
                    newTransaction.setType(ETransactionType.PAYMENT);
                    newTransaction.setDescription(StringUtils.hasText(paymentRequest.getDescription()) ? paymentRequest.getDescription() : "");
                    newTransaction.setAccount(account);
                    Transaction transactionCreated = transactionRepository.save(newTransaction);
                    account.setBalance(account.getBalance() - paymentRequest.getAmount());
                    accountRepository.save(account);
                    return new TransactionResponseDto(
                            user.getEmail(),
                            account.getId(),
                            transactionCreated.getId(),
                            paymentRequest.getCurrency(),
                            ETransactionType.PAYMENT.name(),
                            transactionCreated.getAmount(),
                            transactionCreated.getDescription(),
                            transactionCreated.getTransactionDate()
                    );
                }
            }
        }
        return null;
    }

    @Override
    public SendTransactionResponseDto sendArs(SendTransactionRequestDto transactionRequest, String token) {
        String originUserEmail = jwtService.extractUsername(token.substring(7));
        Optional<User> originUserOptional = userRepository.findByEmail(originUserEmail);
        if(originUserOptional.isPresent()){
            User originUser = originUserOptional.get();
            Optional<Account> originAccountOptional = originUser.getAccounts().stream()
                    .filter(account -> account.getCurrency() == ECurrency.ARS)
                    .findFirst();
            if(originAccountOptional.isPresent()){
                Account originAccount = originAccountOptional.get();
                if(originAccount.getBalance() >= transactionRequest.getAmount() && originAccount.getTransactionLimit() >= transactionRequest.getAmount() && transactionRequest.getAmount() >= 0.0){
                    Optional<Account> destinyAccountOptional = accountRepository.findById(transactionRequest.getDestinyAccountId());
                    if(destinyAccountOptional.isPresent() && destinyAccountOptional.get().getCurrency() == ECurrency.ARS){
                        Account destinyAccount = destinyAccountOptional.get();
                        User destinyUser = destinyAccount.getUser();
                        Transaction paymentTransaction = createPaymentForOriginUser(originAccount,transactionRequest);
                        createIncomeForDestinyUser(destinyAccount,transactionRequest);
                        return new SendTransactionResponseDto(
                                originUser.getEmail(),
                                destinyUser.getEmail(),
                                originAccount.getId(),
                                destinyAccount.getId(),
                                paymentTransaction.getId(),
                                paymentTransaction.getAmount(),
                                paymentTransaction.getType().name(),
                                paymentTransaction.getDescription(),
                                paymentTransaction.getTransactionDate()
                        );
                    }
                }
            }
        }
        return null;
    }

    @Override
    public SendTransactionResponseDto sendUsd(SendTransactionRequestDto transactionRequest, String token) {
        String originUserEmail = jwtService.extractUsername(token.substring(7));
        Optional<User> originUserOptional = userRepository.findByEmail(originUserEmail);
        if(originUserOptional.isPresent()){
            User originUser = originUserOptional.get();
            Optional<Account> originAccountOptional = originUser.getAccounts().stream()
                    .filter(account -> account.getCurrency() == ECurrency.USD)
                    .findFirst();
            if(originAccountOptional.isPresent()){
                Account originAccount = originAccountOptional.get();
                if(originAccount.getBalance() >= transactionRequest.getAmount() && originAccount.getTransactionLimit() >= transactionRequest.getAmount() && transactionRequest.getAmount() >= 0.0){
                    Optional<Account> destinyAccountOptional = accountRepository.findById(transactionRequest.getDestinyAccountId());
                    if(destinyAccountOptional.isPresent() && destinyAccountOptional.get().getCurrency() == ECurrency.USD){
                        Account destinyAccount = destinyAccountOptional.get();
                        User destinyUser = destinyAccount.getUser();
                        Transaction paymentTransaction = createPaymentForOriginUser(originAccount,transactionRequest);
                        createIncomeForDestinyUser(destinyAccount,transactionRequest);
                        return new SendTransactionResponseDto(
                                originUser.getEmail(),
                                destinyUser.getEmail(),
                                originAccount.getId(),
                                destinyAccount.getId(),
                                paymentTransaction.getId(),
                                paymentTransaction.getAmount(),
                                paymentTransaction.getType().name(),
                                paymentTransaction.getDescription(),
                                paymentTransaction.getTransactionDate()
                        );
                    }
                }
            }
        }
        return null;
    }

    private void createIncomeForDestinyUser(Account destinyAccount, SendTransactionRequestDto transactionRequest) {
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transactionRequest.getAmount());
        newTransaction.setType(ETransactionType.INCOME);
        String description = transactionRequest.getDescription().isBlank() ? "" : transactionRequest.getDescription();
        newTransaction.setDescription(description);
        newTransaction.setAccount(destinyAccount);
        transactionRepository.save(newTransaction);
        destinyAccount.setBalance(destinyAccount.getBalance() + transactionRequest.getAmount());
        accountRepository.save(destinyAccount);
    }

    private Transaction createPaymentForOriginUser(Account originAccount, SendTransactionRequestDto transactionRequest) {
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transactionRequest.getAmount());
        newTransaction.setType(ETransactionType.PAYMENT);
        String description = transactionRequest.getDescription().isBlank() ? "" : transactionRequest.getDescription();
        newTransaction.setDescription(description);
        newTransaction.setAccount(originAccount);
        Transaction transactionCreated = transactionRepository.save(newTransaction);
        originAccount.setBalance(originAccount.getBalance() - transactionRequest.getAmount());
        accountRepository.save(originAccount);
        return transactionCreated;
    }


}
