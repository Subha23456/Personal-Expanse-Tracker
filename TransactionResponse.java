package com.portfolio.expensetracker.dto;

import com.portfolio.expensetracker.model.Transaction;
import com.portfolio.expensetracker.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

/** What the API returns. Keeps the database entity out of the public API shape. */
public record TransactionResponse(
        Long id,
        TransactionType type,
        String title,
        BigDecimal amount,
        String category,
        LocalDate transactionDate,
        String notes
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(t.getId(), t.getType(), t.getTitle(), t.getAmount(),
                t.getCategory(), t.getTransactionDate(), t.getNotes());
    }
}
