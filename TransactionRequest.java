package com.portfolio.expensetracker.dto;

import com.portfolio.expensetracker.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** What the client sends when creating or updating a transaction. */
public record TransactionRequest(
        @NotNull(message = "Type is required (INCOME or EXPENSE)")
        TransactionType type,

        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be 100 characters or fewer")
        String title,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        @Digits(integer = 10, fraction = 2, message = "Amount allows up to 10 digits and 2 decimal places")
        BigDecimal amount,

        @NotBlank(message = "Category is required")
        @Size(max = 50, message = "Category must be 50 characters or fewer")
        String category,

        @NotNull(message = "Date is required (format YYYY-MM-DD)")
        LocalDate transactionDate,

        @Size(max = 500, message = "Notes must be 500 characters or fewer")
        String notes
) {
}
