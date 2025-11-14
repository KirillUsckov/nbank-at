package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.Transaction;
import ru.kduskov.api.enums.TransactionType;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TransactionAssert extends BaseAssert<TransactionAssert, Transaction> {

    public TransactionAssert(Transaction actual, SoftAssertions softly) {
        super(actual, TransactionAssert.class, softly);
    }

    public static TransactionAssert assertThat(Transaction actual, SoftAssertions softly) {
        return new TransactionAssert(actual, softly);
    }

    public TransactionAssert matchId() {
        softly(() ->
                softly.assertThat(actual.getId())
                        .withFailMessage("Transaction id should be positive")
                        .isPositive()
        );
        return this;
    }

    public TransactionAssert matchAmount(int expectedAmount) {
        softly(() ->
                softly.assertThat(actual.getAmount())
                        .withFailMessage("Expected amount %s but was %s", expectedAmount, actual.getAmount())
                        .isEqualTo(expectedAmount)
        );
        return this;
    }

    public TransactionAssert matchType(TransactionType expectedType) {
        softly(() ->
                softly.assertThat(actual.getType())
                        .withFailMessage("Expected type %s but was %s", expectedType, actual.getType())
                        .isEqualTo(expectedType)
        );
        return this;
    }

    public TransactionAssert matchRelatedAccountId(int expectedAccountId) {
        softly(() ->
                softly.assertThat(actual.getRelatedAccountId())
                        .withFailMessage("Expected related account id %s but was %s",
                                expectedAccountId, actual.getRelatedAccountId())
                        .isEqualTo(expectedAccountId)
        );
        return this;
    }

    public TransactionAssert hasValidTimestamp() {
        softly(() -> {
            var moscowTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
            ZonedDateTime serverMoscowTime = actual.getTimestamp()
                    .atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of("Europe/Moscow"));

            long secondsDiff = Math.abs(Duration.between(serverMoscowTime, moscowTime).getSeconds());

            softly.assertThat(secondsDiff)
                    .withFailMessage("Transaction time difference is more than 30 seconds. Difference: %ss", secondsDiff)
                    .isLessThanOrEqualTo(30);
        });
        return this;
    }

    public TransactionAssert matches(int expectedAmount,
                                     TransactionType expectedType,
                                     Integer expectedRelatedAccountId) {
        return matchAmount(expectedAmount)
                .matchType(expectedType)
                .matchId()
                .hasValidTimestamp()
                .matchRelatedAccountId(expectedRelatedAccountId);
    }
}