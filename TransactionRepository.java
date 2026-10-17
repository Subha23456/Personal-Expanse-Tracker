package com.portfolio.expensetracker.repository;

import com.portfolio.expensetracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * JPA generates parameterized (prepared) SQL for every method here,
 * so user input never gets concatenated into a query string.
 */
public interface TransactionRepository
        extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("select distinct t.category from Transaction t order by t.category")
    List<String> findDistinctCategories();
}
