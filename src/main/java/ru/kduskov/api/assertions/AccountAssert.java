package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.models.body.response.Transaction;
import ru.kduskov.api.enums.TransactionType;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;

public class AccountAssert extends BaseAssert<AccountAssert, AccountResponseBody> {

    public AccountAssert(AccountResponseBody actual, SoftAssertions softly) {
        super(actual, AccountAssert.class, softly);
    }

    public static AccountAssert assertThat(AccountResponseBody actual, SoftAssertions softly) {
        return new AccountAssert(actual, softly);
    }

    public AccountAssert hasId(long expectedId) {
        softly(() ->
                softly.assertThat(actual.getId())
                        .withFailMessage("Expected account id %s but was %s", expectedId, actual.getId())
                        .isEqualTo(expectedId)
        );
        return this;
    }

    public AccountAssert hasBalance(double expectedBalance) {
        softly(() ->
                softly.assertThat(actual.getBalance())
                        .withFailMessage("Expected balance %s but was %s", expectedBalance, actual.getBalance())
                        .isEqualTo(expectedBalance)
        );
        return this;
    }

    public AccountAssert hasAccountNumber(String expectedAccountNumber) {
        softly(() ->
                softly.assertThat(actual.getAccountNumber())
                        .withFailMessage("Expected account number '%s' but was '%s'",
                                expectedAccountNumber, actual.getAccountNumber())
                        .isEqualTo(expectedAccountNumber)
        );
        return this;
    }

    public AccountAssert hasTransactions() {
        softly(() ->
                softly.assertThat(actual.getTransactions())
                        .withFailMessage("Account has no transactions")
                        .isNotEmpty()
        );
        return this;
    }

    public AccountAssert hasTransactionsCount(int expectedCount) {
        softly(() ->
                softly.assertThat(actual.getTransactions())
                        .withFailMessage("Expected %s transactions but found %s",
                                expectedCount, actual.getTransactions().size())
                        .hasSize(expectedCount)
        );
        return this;
    }

    public AccountAssert hasLatestTransaction(double expectedAmount,
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

    public AccountAssert hasLatestDeposit(double expectedAmount, long expectedAccountId) {
        return hasLatestTransaction(expectedAmount, TransactionType.DEPOSIT, expectedAccountId);
    }

    public AccountAssert hasLatestTransferIn(double expectedAmount, long expectedRelatedAccountId) {
        return hasLatestTransaction(expectedAmount, TransactionType.TRANSFER_IN, expectedRelatedAccountId);
    }

    public AccountAssert hasLatestTransferOut(double expectedAmount, long expectedRelatedAccountId) {
        return hasLatestTransaction(expectedAmount, TransactionType.TRANSFER_OUT, expectedRelatedAccountId);
    }

    public AccountAssert matches(AccountResponseBody expectedAccount) {
        return hasId(expectedAccount.getId())
                .hasBalance(expectedAccount.getBalance())
                .hasAccountNumber(expectedAccount.getAccountNumber());
    }

    public AccountAssert wasNotChangedComparedTo(AccountResponseBody originalAccount) {
        return matches(originalAccount);
    }

    public AccountAssert wasIncreasedBy(double transactionAmount, AccountResponseBody originalAccount) {
        var expectedBalance = originalAccount.getBalance() + transactionAmount;
        return hasBalance(expectedBalance)
                .hasAccountNumber(originalAccount.getAccountNumber());
    }

    public AccountAssert wasDencreasedBy(double transactionAmount, AccountResponseBody originalAccount) {
        var expectedBalance = originalAccount.getBalance() - transactionAmount;
        return hasBalance(expectedBalance)
                .hasAccountNumber(originalAccount.getAccountNumber());
    }

    public AccountAssert isValidDepositResponse(double depositAmount, long accountId, AccountResponseBody originalAccount) {

        return hasId(accountId)
                .hasBalance(originalAccount.getBalance())
                .hasAccountNumber(originalAccount.getAccountNumber())
                .hasLatestDeposit(depositAmount, accountId);
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

    public AccountAssert hasNoTransactionOfType(TransactionType unexpectedType) {
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