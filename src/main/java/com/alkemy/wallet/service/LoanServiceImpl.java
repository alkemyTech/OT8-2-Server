package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.request.LoanRequestDto;
import com.alkemy.wallet.dto.response.LoanResponseDto;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements ILoanService{
    @Override
    public LoanResponseDto simulateLoan(LoanRequestDto loanRequest) {
        double amount = loanRequest.getAmount();
        int months = loanRequest.getMonths();
        if(amount > 0.0 && months > 0){
            return calculateLoan(amount,months);
        }
        return null;
    }

    private LoanResponseDto calculateLoan(double amount, int months) {
        double interest = 0.05;
        double paymentPerMonth = amount/months + amount * interest;
        double totalInterest = (amount*interest)*months;
        double totalPayment = amount + totalInterest;
        return new LoanResponseDto(
                amount,
                months,
                "5% monthly",
                paymentPerMonth,
                totalInterest,
                totalPayment
        );
    }
}
