package com.oleksandr.financeplatform.repository;

import com.oleksandr.financeplatform.entity.Transaction;
import com.oleksandr.financeplatform.entity.TransactionType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
            select transaction from Transaction transaction
            where transaction.user.id = :userId
              and (:categoryId is null or transaction.category.id = :categoryId)
              and (:type is null or transaction.type = :type)
              and (:fromDate is null or transaction.transactionDate >= :fromDate)
              and (:toDate is null or transaction.transactionDate <= :toDate)
            order by transaction.transactionDate desc, transaction.id desc
            """)
    List<Transaction> findAllByFilters(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    List<Transaction> findAllByUserIdAndTransactionDateBetween(Long userId, LocalDate fromDate, LocalDate toDate);
}
