package com.portfolio.expensetracker.repository;

import com.portfolio.expensetracker.dto.TransactionFilter;
import com.portfolio.expensetracker.model.Transaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Builds the dynamic WHERE clause from whichever filters were provided. */
public final class TransactionSpecifications {

    private TransactionSpecifications() {
    }

    public static Specification<Transaction> withFilters(TransactionFilter f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (f.type() != null) {
                predicates.add(cb.equal(root.get("type"), f.type()));
            }
            if (f.category() != null) {
                predicates.add(cb.equal(cb.lower(root.get("category")), f.category().toLowerCase(Locale.ROOT)));
            }
            if (f.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), f.startDate()));
            }
            if (f.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), f.endDate()));
            }
            if (f.keyword() != null) {
                String pattern = "%" + escapeLike(f.keyword().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern, '\\'),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("notes"), "")), pattern, '\\')
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Make "%" and "_" typed by the user match literally instead of acting as wildcards.
    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
