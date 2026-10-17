package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.dto.CategoryTotal;
import com.portfolio.expensetracker.dto.SummaryResponse;
import com.portfolio.expensetracker.dto.TransactionFilter;
import com.portfolio.expensetracker.dto.TransactionRequest;
import com.portfolio.expensetracker.dto.TransactionResponse;
import com.portfolio.expensetracker.exception.BadRequestException;
import com.portfolio.expensetracker.exception.TransactionNotFoundException;
import com.portfolio.expensetracker.model.Transaction;
import com.portfolio.expensetracker.model.TransactionType;
import com.portfolio.expensetracker.repository.TransactionRepository;
import com.portfolio.expensetracker.repository.TransactionSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Business logic lives here: CRUD, filter clean-up, and totals. */
@Service
public class TransactionService {

    private static final Sort NEWEST_FIRST =
            Sort.by(Sort.Direction.DESC, "transactionDate").and(Sort.by(Sort.Direction.DESC, "id"));

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll(TransactionFilter filter) {
        return loadFiltered(filter).stream().map(TransactionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(Long id) {
        return TransactionResponse.from(getOrThrow(id));
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        Transaction t = new Transaction();
        apply(t, request);
        return TransactionResponse.from(repository.save(t));
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction t = getOrThrow(id);
        apply(t, request);
        return TransactionResponse.from(repository.save(t));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<String> findCategories() {
        return repository.findDistinctCategories();
    }

    /** Totals for the transactions matching the filter. Use TransactionFilter.none() for all-time totals. */
    @Transactional(readOnly = true)
    public SummaryResponse summarize(TransactionFilter filter) {
        List<Transaction> transactions = loadFiltered(filter);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        // Group expenses by category ignoring case, so "Food" and "food" are one bucket.
        Map<String, String> displayNames = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseTotals = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                income = income.add(t.getAmount());
            } else {
                expenses = expenses.add(t.getAmount());
                String key = t.getCategory().toLowerCase(Locale.ROOT);
                displayNames.putIfAbsent(key, t.getCategory());
                expenseTotals.merge(key, t.getAmount(), BigDecimal::add);
            }
        }

        List<CategoryTotal> byCategory = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : expenseTotals.entrySet()) {
            double pct = expenses.signum() == 0 ? 0
                    : e.getValue().multiply(BigDecimal.valueOf(100))
                            .divide(expenses, 1, RoundingMode.HALF_UP).doubleValue();
            byCategory.add(new CategoryTotal(displayNames.get(e.getKey()), money(e.getValue()), pct));
        }
        byCategory.sort(Comparator.comparing(CategoryTotal::total).reversed());

        return new SummaryResponse(money(income), money(expenses), money(income.subtract(expenses)),
                transactions.size(), byCategory);
    }

    // ---- helpers ----

    private List<Transaction> loadFiltered(TransactionFilter raw) {
        TransactionFilter filter = normalize(raw);
        if (filter.startDate() != null && filter.endDate() != null
                && filter.startDate().isAfter(filter.endDate())) {
            throw new BadRequestException("startDate must not be after endDate");
        }
        return repository.findAll(TransactionSpecifications.withFilters(filter), NEWEST_FIRST);
    }

    // Blank strings from query params (?category=) are treated as "no filter".
    private TransactionFilter normalize(TransactionFilter f) {
        return new TransactionFilter(f.type(), blankToNull(f.category()), f.startDate(), f.endDate(), blankToNull(f.keyword()));
    }

    private void apply(Transaction t, TransactionRequest r) {
        t.setType(r.type());
        t.setTitle(r.title().trim());
        t.setAmount(r.amount().setScale(2, RoundingMode.HALF_UP));
        t.setCategory(r.category().trim());
        t.setTransactionDate(r.transactionDate());
        t.setNotes(blankToNull(r.notes()));
    }

    private Transaction getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static BigDecimal money(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
