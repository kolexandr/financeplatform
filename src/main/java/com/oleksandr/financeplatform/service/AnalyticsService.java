package com.oleksandr.financeplatform.service;

import com.oleksandr.financeplatform.dto.analytics.CategorySpendingResponse;
import com.oleksandr.financeplatform.dto.analytics.MonthlySummaryResponse;
import com.oleksandr.financeplatform.entity.Budget;
import com.oleksandr.financeplatform.entity.Category;
import com.oleksandr.financeplatform.entity.Transaction;
import com.oleksandr.financeplatform.entity.TransactionType;
import com.oleksandr.financeplatform.entity.User;
import com.oleksandr.financeplatform.repository.BudgetRepository;
import com.oleksandr.financeplatform.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final CurrentUserService currentUserService;

    public AnalyticsService(
            TransactionRepository transactionRepository,
            BudgetRepository budgetRepository,
            CurrentUserService currentUserService
    ) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(YearMonth month) {
        User user = currentUserService.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findAllByUserIdAndTransactionDateBetween(
                user.getId(), month.atDay(1), month.atEndOfMonth());
        List<Budget> budgets = budgetRepository.findAllByUserIdAndMonth(user.getId(), month);

        BigDecimal totalIncome = totalForType(transactions, TransactionType.INCOME);
        BigDecimal totalExpenses = totalForType(transactions, TransactionType.EXPENSE);
        BigDecimal totalBudget = budgets.stream()
                .map(Budget::getLimit)
                .reduce(ZERO, BigDecimal::add);

        return new MonthlySummaryResponse(
                month,
                totalIncome,
                totalExpenses,
                totalIncome.subtract(totalExpenses),
                totalBudget,
                totalBudget.subtract(totalExpenses),
                expensesByCategory(transactions)
        );
    }

    private BigDecimal totalForType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == type)
                .map(Transaction::getAmount)
                .reduce(ZERO, BigDecimal::add);
    }

    private List<CategorySpendingResponse> expensesByCategory(List<Transaction> transactions) {
        Map<Category, BigDecimal> spendingByCategory = new HashMap<>();
        transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .forEach(transaction -> spendingByCategory.merge(
                        transaction.getCategory(), transaction.getAmount(), BigDecimal::add));

        return spendingByCategory.entrySet().stream()
                .map(entry -> new CategorySpendingResponse(
                        entry.getKey().getId(), entry.getKey().getName(), entry.getValue()))
                .sorted(Comparator.comparing(CategorySpendingResponse::categoryName))
                .toList();
    }
}
