package com.portfolio.expensetracker.dto;

import com.portfolio.expensetracker.model.TransactionType;

import java.time.LocalDate;

/** Optional filters. A null field means "do not filter on this". */
public record TransactionFilter(
        TransactionType type,
        String category,
        LocalDate startDate,
        LocalDate endDate,
        String keyword
) {
    public static TransactionFilter none() {
        return new TransactionFilter(null, null, null, null, null);
    }
}
