package ru.kduskov.api.assertions;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.response.Transaction;
import ru.kduskov.api.models.body.response.accounts.TransactionsResponseBody;
import ru.kduskov.common.utils.DateTimeUtils;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;

public class TransactionAssert extends BaseAssert<TransactionAssert, Transaction> {

    public TransactionAssert(Transaction actual, SoftAssertions softly) {
        super(actual, TransactionAssert.class, softly);
    }

    public static TransactionAssert assertThat(Transaction actual, SoftAssertions softly) {
        return new TransactionAssert(actual, softly);
    }

    public TransactionAssert hasBalance(Double expectedAmount) {
        isEqualTo(
                actual.getAmount(),
                expectedAmount,
                String.format("Expected transaction amount %s but was %s",
                        expectedAmount, actual.getAmount())
        );
        return this;
    }

    public TransactionAssert hasTransactionType(TransactionType expectedType) {
        isEqualTo(
                actual.getType(),
                expectedType,
                String.format("Expected transaction type %s but was %s",
                        expectedType, actual.getType())
        );
        return this;
    }

    private TransactionAssert hasTimestampCloseToNow() {
        long secondsDiff = DateTimeUtils.differenceWithCurrentMoscowTimeInSeconds(
                actual.getTimestamp()
        );

        softly.assertThat(secondsDiff)
                .withFailMessage("Transaction time difference is more than 30 seconds. Difference: %ss", secondsDiff)
                .isLessThanOrEqualTo(30);
        return this;
    }

    public TransactionAssert hasRelatedAccountId(Long expectedRelatedAccountId) {
        isEqualTo(
                actual.getRelatedAccountId(),
                expectedRelatedAccountId,
                String.format("Expected related account id %s but was %s",
                        expectedRelatedAccountId, actual.getRelatedAccountId())
        );
        return this;
    }

    @Step("Check transaction has expected values")
    public TransactionAssert matches(double expectedAmount,
                                     TransactionType expectedType,
                                     Long expectedRelatedAccountId) {
        return hasBalance(expectedAmount)
                .hasTransactionType(expectedType)
                .hasRelatedAccountId(expectedRelatedAccountId)
                .hasTimestampCloseToNow();
    }

    public TransactionAssert isDeposit(double expectedAmount, long expectedAccountId) {
        return matches(expectedAmount, TransactionType.DEPOSIT, expectedAccountId);
    }

    public TransactionAssert isTransferIn(double expectedAmount, long expectedRelatedAccountId) {
        return matches(expectedAmount, TransactionType.TRANSFER_IN, expectedRelatedAccountId);
    }

    public TransactionAssert isTransferOut(double expectedAmount, long expectedRelatedAccountId) {
        return matches(expectedAmount, TransactionType.TRANSFER_OUT, expectedRelatedAccountId);
    }
}