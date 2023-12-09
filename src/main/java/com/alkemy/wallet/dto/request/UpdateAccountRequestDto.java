package com.alkemy.wallet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateAccountRequestDto {
    @Positive
    private double newTransactionLimit;
}
