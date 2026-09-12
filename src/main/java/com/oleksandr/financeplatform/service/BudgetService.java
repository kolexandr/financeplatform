package com.oleksandr.financeplatform.service;

import com.oleksandr.financeplatform.dto.budget.BudgetRequest;
import com.oleksandr.financeplatform.dto.budget.BudgetResponse;
import com.oleksandr.financeplatform.entity.Budget;
import com.oleksandr.financeplatform.entity.Category;
import com.oleksandr.financeplatform.entity.User;
import com.oleksandr.financeplatform.exception.DuplicateResourceException;
import com.oleksandr.financeplatform.exception.ResourceNotFoundException;
import com.oleksandr.financeplatform.repository.BudgetRepository;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    public BudgetService(
            BudgetRepository budgetRepository,
            CategoryService categoryService,
            CurrentUserService currentUserService
    ) {
        this.budgetRepository = budgetRepository;
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public BudgetResponse create(BudgetRequest request) {
        User user = currentUserService.getCurrentUser();
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonth(user.getId(), request.categoryId(), request.month())) {
            throw new DuplicateResourceException("A budget already exists for this category and month");
        }

        Budget budget = new Budget();
        budget.setUser(user);
        applyRequest(budget, request);
        return toResponse(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getAll() {
        User user = currentUserService.getCurrentUser();
        return budgetRepository.findAllByUserIdOrderByMonthDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getForMonth(YearMonth month) {
        User user = currentUserService.getCurrentUser();
        return budgetRepository.findAllByUserIdAndMonth(user.getId(), month).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetResponse getById(Long id) {
        return toResponse(getOwnedBudget(id));
    }

    @Transactional
    public BudgetResponse update(Long id, BudgetRequest request) {
        Budget budget = getOwnedBudget(id);
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndIdNot(
                budget.getUser().getId(), request.categoryId(), request.month(), id)) {
            throw new DuplicateResourceException("A budget already exists for this category and month");
        }
        applyRequest(budget, request);
        return toResponse(budget);
    }

    @Transactional
    public void delete(Long id) {
        budgetRepository.delete(getOwnedBudget(id));
    }

    private Budget getOwnedBudget(Long id) {
        User user = currentUserService.getCurrentUser();
        return budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget"));
    }

    private void applyRequest(Budget budget, BudgetRequest request) {
        Category category = categoryService.getOwnedCategory(request.categoryId());
        budget.setCategory(category);
        budget.setLimit(request.amount());
        budget.setMonth(request.month());
    }

    private BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getLimit(),
                budget.getMonth()
        );
    }
}
