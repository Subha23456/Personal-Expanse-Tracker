package com.portfolio.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

/** Totals for a set of transactions (all of them, or only those matching filters). */
public record SummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance,
        long transactionCount,
        List<CategoryTotal> expenseByCategory
) {
}
