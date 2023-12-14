package com.alkemy.wallet.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SendTransactionRequestDto {
    @NotNull
    private Long destinyAccountId;
    @NotNull
    @Positive
    private Double Amount;
    private String description;
}
