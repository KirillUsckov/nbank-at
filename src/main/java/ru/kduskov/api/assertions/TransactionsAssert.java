package ru.kduskov.api.assertions;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.response.Transaction;
import ru.kduskov.api.models.body.response.accounts.TransactionsResponseBody;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;

public class TransactionsAssert extends BaseAssert<TransactionsAssert, TransactionsResponseBody> {

    public TransactionsAssert(TransactionsResponseBody actual, SoftAssertions softly) {
        super(actual, TransactionsAssert.class, softly);
    }

    public static TransactionsAssert assertThat(TransactionsResponseBody actual, SoftAssertions softly) {
        return new TransactionsAssert(actual, softly);
    }

    @Step("Check transaction list is not empty")
    public TransactionsAssert hasTransactions() {
        softly(() ->
                softly.assertThat(actual.getTransactions())
                        .withFailMessage("Account has no transactions")
                        .isNotEmpty()
        );
        return this;
    }

    @Step("Check transaction list has no transaction of {unexpectedType} type")
    public TransactionsAssert hasNoTransactionOfType(TransactionType unexpectedType) {
        softly(() -> {
            boolean hasType = actual.getTransactions().stream()
                    .anyMatch(transaction -> transaction.getType() == unexpectedType);

            softly.assertThat(hasType)
                    .withFailMessage("Found unexpected transaction with type %s", unexpectedType)
                    .isFalse();
        });
        return this;
    }
}