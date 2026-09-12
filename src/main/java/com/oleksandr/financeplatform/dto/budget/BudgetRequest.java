package com.oleksandr.financeplatform.dto.budget;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetRequest(
        @NotNull Long categoryId,
        @NotNull @Positive BigDecimal amount,
        @NotNull @JsonFormat(pattern = "yyyy-MM") YearMonth month
) {
}
