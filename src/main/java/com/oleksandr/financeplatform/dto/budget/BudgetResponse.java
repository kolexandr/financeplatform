package com.oleksandr.financeplatform.dto.budget;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        BigDecimal amount,
        @JsonFormat(pattern = "yyyy-MM") YearMonth month
) {
}
