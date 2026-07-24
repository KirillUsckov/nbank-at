package ru.kduskov.api.utils;

import lombok.experimental.UtilityClass;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.response.Transaction;

import java.util.Comparator;
import java.util.List;

public class TransactionsListUtils {
    private TransactionsListUtils() {}

    public static Transaction findLatestTransaction(List<Transaction> transactions) {
        return transactions.stream()
                .max(Comparator.comparing(Transaction::getId))
                .orElseThrow(() -> new IllegalStateException("Transaction list is empty"));
    }

    public static Transaction findFirstTransactionWithType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(tr -> tr.getType().equals(type))
                .findFirst().orElseThrow(() -> new IllegalStateException("Transaction list doesnt contains transaction with type " + type));
    }
}
