package com.oleksandr.financeplatform.service;

import com.oleksandr.financeplatform.dto.transaction.TransactionRequest;
import com.oleksandr.financeplatform.dto.transaction.TransactionResponse;
import com.oleksandr.financeplatform.entity.Category;
import com.oleksandr.financeplatform.entity.Transaction;
import com.oleksandr.financeplatform.entity.TransactionType;
import com.oleksandr.financeplatform.entity.User;
import com.oleksandr.financeplatform.exception.InvalidDateRangeException;
import com.oleksandr.financeplatform.exception.ResourceNotFoundException;
import com.oleksandr.financeplatform.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryService categoryService,
            CurrentUserService currentUserService
    ) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        User user = currentUserService.getCurrentUser();
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        applyRequest(transaction, request);
        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll(
            Long categoryId,
            TransactionType type,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new InvalidDateRangeException();
        }
        User user = currentUserService.getCurrentUser();
        return transactionRepository.findAllByFilters(user.getId(), categoryId, type, fromDate, toDate).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(Long id) {
        return toResponse(getOwnedTransaction(id));
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction transaction = getOwnedTransaction(id);
        applyRequest(transaction, request);
        return toResponse(transaction);
    }

    @Transactional
    public void delete(Long id) {
        transactionRepository.delete(getOwnedTransaction(id));
    }

    private Transaction getOwnedTransaction(Long id) {
        User user = currentUserService.getCurrentUser();
        return transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction"));
    }

    private void applyRequest(Transaction transaction, TransactionRequest request) {
        Category category = categoryService.getOwnedCategory(request.categoryId());
        transaction.setCategory(category);
        transaction.setProvider(request.provider().trim());
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setDescription(request.description());
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getProvider(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}
