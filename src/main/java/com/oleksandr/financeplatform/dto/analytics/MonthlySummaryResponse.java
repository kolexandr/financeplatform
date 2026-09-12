package com.oleksandr.financeplatform.dto.analytics;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record MonthlySummaryResponse(
        @JsonFormat(pattern = "yyyy-MM") YearMonth month,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal currentBalance,
        BigDecimal totalBudget,
        BigDecimal remainingBudget,
        List<CategorySpendingResponse> expensesByCategory
) {
}
