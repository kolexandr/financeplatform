package com.oleksandr.financeplatform.dto.transaction;

import com.oleksandr.financeplatform.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String provider,
        BigDecimal amount,
        TransactionType type,
        LocalDate transactionDate,
        String description,
        LocalDateTime createdAt
) {
}
