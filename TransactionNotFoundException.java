package com.portfolio.expensetracker.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction with id " + id + " was not found");
    }
}
