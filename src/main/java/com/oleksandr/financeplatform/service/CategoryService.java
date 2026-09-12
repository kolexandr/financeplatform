package com.oleksandr.financeplatform.service;

import com.oleksandr.financeplatform.dto.category.CategoryRequest;
import com.oleksandr.financeplatform.dto.category.CategoryResponse;
import com.oleksandr.financeplatform.entity.Category;
import com.oleksandr.financeplatform.entity.User;
import com.oleksandr.financeplatform.exception.CategoryInUseException;
import com.oleksandr.financeplatform.exception.DuplicateResourceException;
import com.oleksandr.financeplatform.exception.ResourceNotFoundException;
import com.oleksandr.financeplatform.repository.BudgetRepository;
import com.oleksandr.financeplatform.repository.CategoryRepository;
import com.oleksandr.financeplatform.repository.TransactionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final CurrentUserService currentUserService;

    public CategoryService(
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            BudgetRepository budgetRepository,
            CurrentUserService currentUserService
    ) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        User user = currentUserService.getCurrentUser();
        String name = request.name().trim();
        if (categoryRepository.existsByUserIdAndNameIgnoreCase(user.getId(), name)) {
            throw new DuplicateResourceException("A category with this name already exists");
        }

        Category category = new Category();
        category.setUser(user);
        category.setName(name);
        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        User user = currentUserService.getCurrentUser();
        return categoryRepository.findAllByUserIdOrderByNameAsc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return toResponse(getOwnedCategory(id));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getOwnedCategory(id);
        String name = request.name().trim();
        if (categoryRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(
                category.getUser().getId(), name, id)) {
            throw new DuplicateResourceException("A category with this name already exists");
        }
        category.setName(name);
        return toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getOwnedCategory(id);
        if (transactionRepository.existsByCategoryId(id) || budgetRepository.existsByCategoryId(id)) {
            throw new CategoryInUseException();
        }
        categoryRepository.delete(category);
    }

    public Category getOwnedCategory(Long id) {
        User user = currentUserService.getCurrentUser();
        return categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category"));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
