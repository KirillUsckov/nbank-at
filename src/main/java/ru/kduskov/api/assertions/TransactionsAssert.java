package ru.kduskov.api.assertions;

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

    public TransactionsAssert hasTransactions() {
        softly(() ->
                softly.assertThat(actual.getTransactions())
                        .withFailMessage("Account has no transactions")
                        .isNotEmpty()
        );
        return this;
    }

    public TransactionsAssert hasTransactionsCount(int expectedCount) {
        softly(() ->
                softly.assertThat(actual.getTransactions())
                        .withFailMessage("Expected %s transactions but found %s",
                                expectedCount, actual.getTransactions().size())
                        .hasSize(expectedCount)
        );
        return this;
    }

    public TransactionsAssert hasLatestTransaction(double expectedAmount,
                                                   TransactionType expectedType,
                                                   Long expectedRelatedAccountId) {
        isNotNull();
        hasTransactions();

        var latestTransaction = actual.getTransactions().stream()
                .max(Comparator.comparing(Transaction::getId));

        softly(() -> {
            softly.assertThat(latestTransaction)
                    .withFailMessage("No transactions found")
                    .isPresent();

            if (latestTransaction.isPresent()) {
                validateTransaction(latestTransaction.get(), expectedAmount, expectedType, expectedRelatedAccountId);
            }
        });

        return this;
    }

    public TransactionsAssert hasLatestDeposit(double expectedAmount, long expectedAccountId) {
        return hasLatestTransaction(expectedAmount, TransactionType.DEPOSIT, expectedAccountId);
    }

    public TransactionsAssert hasLatestTransferIn(double expectedAmount, long expectedRelatedAccountId) {
        return hasLatestTransaction(expectedAmount, TransactionType.TRANSFER_IN, expectedRelatedAccountId);
    }

    public TransactionsAssert hasLatestTransferOut(double expectedAmount, long expectedRelatedAccountId) {
        return hasLatestTransaction(expectedAmount, TransactionType.TRANSFER_OUT, expectedRelatedAccountId);
    }

    public TransactionsAssert isValidDepositResponse(double depositAmount, long accountId) {
        return hasLatestDeposit(depositAmount, accountId);
    }

    private void validateTransaction(Transaction transaction,
                                     double expectedAmount,
                                     TransactionType expectedType,
                                     Long expectedRelatedAccountId) {
        softly.assertThat(transaction.getAmount())
                .withFailMessage("Expected transaction amount %s but was %s",
                        expectedAmount, transaction.getAmount())
                .isEqualTo(expectedAmount);

        softly.assertThat(transaction.getType())
                .withFailMessage("Expected transaction type %s but was %s",
                        expectedType, transaction.getType())
                .isEqualTo(expectedType);

        if (expectedRelatedAccountId != null) {
            softly.assertThat(transaction.getRelatedAccountId())
                    .withFailMessage("Expected related account id %s but was %s",
                            expectedRelatedAccountId, transaction.getRelatedAccountId())
                    .isEqualTo(expectedRelatedAccountId);
        }

        validateTransactionTime(transaction);
    }

    private void validateTransactionTime(Transaction transaction) {
        ZonedDateTime moscowTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        ZonedDateTime serverMoscowTime = transaction.getTimestamp()
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        long secondsDiff = Math.abs(Duration.between(serverMoscowTime, moscowTime).getSeconds());

        softly.assertThat(secondsDiff)
                .withFailMessage("Transaction time difference is more than 30 seconds. Difference: %ss", secondsDiff)
                .isLessThanOrEqualTo(30);
    }

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