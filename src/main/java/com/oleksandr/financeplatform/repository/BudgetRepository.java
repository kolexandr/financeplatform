package com.oleksandr.financeplatform.repository;

import com.oleksandr.financeplatform.entity.Budget;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findAllByUserIdOrderByMonthDesc(Long userId);

    List<Budget> findAllByUserIdAndMonth(Long userId, YearMonth month);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndCategoryIdAndMonth(Long userId, Long categoryId, YearMonth month);

    boolean existsByUserIdAndCategoryIdAndMonthAndIdNot(Long userId, Long categoryId, YearMonth month, Long id);

    boolean existsByCategoryId(Long categoryId);
}
