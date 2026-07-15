package ru.kduskov.db.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.models.dao.BaseDao;
import ru.kduskov.db.models.dao.TransactionDao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDaoAssert extends BaseDbAssert<TransactionDaoAssert, TransactionDao> {
    public TransactionDaoAssert(TransactionDao actual, SoftAssertions softly) {
        super(actual, TransactionDaoAssert.class, softly);
    }

    public static TransactionDaoAssert assertThat(TransactionDao actual, SoftAssertions softly) {
        return new TransactionDaoAssert(actual, softly);
    }

    public TransactionDaoAssert isEqualTo(TransactionDao expected) {
        return (TransactionDaoAssert) accountIdEquals(expected.getAccountId())
                .amountEquals(expected.getAmount())
                .typeEquals(expected.getType())
                .timestampEquals(expected.getTimestamp())
                .relatedAccountIdEquals(expected.getRelatedAccountId())
                .idEquals(expected.getId())
                .dateCreatedEquals(expected.getCreatedAt());
    }

    public TransactionDaoAssert isEqualTo(Long id, Long senderAccId, Long receiverAccId, BigDecimal amount, TransactionType type) {
        return (TransactionDaoAssert) accountIdEquals(senderAccId)
                .amountEquals(amount)
                .typeEquals(type)
                .timestampBefore(LocalDateTime.now())
                .relatedAccountIdEquals(receiverAccId)
                .idEquals(id)
                .dateCreatedBefore(LocalDateTime.now());
    }

    private TransactionDaoAssert timestampBefore(LocalDateTime expectedTimestamp) {
        softly.assertThat(actual.getTimestamp()).withFailMessage(String.format("Expected timestamp %s should be after %s", expectedTimestamp, actual.getTimestamp())).isBefore(expectedTimestamp);
        return this;
    }

    private TransactionDaoAssert timestampEquals(LocalDateTime expectedTimestamp) {
        isEqualTo(actual.getTimestamp(), expectedTimestamp, String.format("Expected timestamp %s, but was %s", expectedTimestamp, actual.getTimestamp()));
        return this;
    }

    public TransactionDaoAssert relatedAccountIdEquals(Long expectedRelatedAccId) {
        isEqualTo(actual.getRelatedAccountId(), expectedRelatedAccId, String.format("Expected related account id %s, but was %s", expectedRelatedAccId, actual.getRelatedAccountId()));
        return this;
    }

    public TransactionDaoAssert typeEquals(TransactionType expectedType) {
        isEqualTo(actual.getType(), expectedType, String.format("Expected type %s, but was %s", expectedType, actual.getType()));
        return this;
    }

    public TransactionDaoAssert accountIdEquals(Long expectedAccId) {
        isEqualTo(actual.getAccountId(), expectedAccId, String.format("Expected account id %s, but was %s", expectedAccId, actual.getAccountId()));
        return this;
    }

    public TransactionDaoAssert amountEquals(BigDecimal expectedAmount) {
        isEqualTo(actual.getAmount().doubleValue(), expectedAmount.doubleValue(), String.format("Expected amount %s, but was %s", expectedAmount, actual.getAmount()));
        return this;
    }
}
