package com.portfolio.expensetracker.dto;

import java.math.BigDecimal;

/** One row of the "spending by category" summary. */
public record CategoryTotal(String category, BigDecimal total, double percentage) {
}
