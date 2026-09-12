package com.oleksandr.financeplatform.dto.analytics;

import java.math.BigDecimal;

public record CategorySpendingResponse(Long categoryId, String categoryName, BigDecimal amount) {
}
