package com.oleksandr.financeplatform.dto.transaction;

import com.oleksandr.financeplatform.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 100) String provider,
        @NotNull @Positive BigDecimal amount,
        @NotNull TransactionType type,
        @NotNull LocalDate transactionDate,
        @Size(max = 500) String description
) {
}
