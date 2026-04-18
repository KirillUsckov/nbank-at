package ru.kduskov.db.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.db.models.dao.AccountDao;

import java.math.BigDecimal;

public class AccountDaoAssert extends BaseDbAssert<AccountDaoAssert, AccountDao> {
    public AccountDaoAssert(AccountDao actual, SoftAssertions softly) {
        super(actual, AccountDaoAssert.class, softly);
    }

    public static AccountDaoAssert assertThat(AccountDao actual, SoftAssertions softly) {
        return new AccountDaoAssert(actual,  softly);
    }

    public AccountDaoAssert isEqualTo(AccountDao expected, boolean isDateUpdatedEquals) {
        var assertion = (AccountDaoAssert) customerIdEquals(expected.getCustomerId())
                .accountNumberEquals(expected.getAccountNumber())
                .balanceEquals(expected.getBalance())
                .idEquals(expected.getId())
                .dateCreatedEquals(expected.getCreatedAt());
        if(isDateUpdatedEquals) {
            assertion.dateUpdatedEquals(expected.getUpdatedAt());
        } else {
            assertion.dateUpdatedAfter(expected.getUpdatedAt());
        }
        return assertion;
    }

    /**
     * isEqualTo with different date updated (actual is after expected)
     * @param expected
     * @return
     */
    public AccountDaoAssert isEqualTo(AccountDao expected) {
        return isEqualTo(expected, false);
    }

    public AccountDaoAssert customerIdEquals(Long expectedCustomerId) {
        isEqualTo(actual.getCustomerId(), expectedCustomerId, String.format("Expected customerId %s but was %s", expectedCustomerId, actual.getCustomerId()));
        return this;
    }

    public AccountDaoAssert accountNumberEquals(String expectedAccNumber) {
        isEqualTo(actual.getAccountNumber(), expectedAccNumber, String.format("Expected account number %s but was %s", expectedAccNumber, actual.getAccountNumber()));
        return this;
    }

    public AccountDaoAssert balanceEquals(BigDecimal expectedBalance) {
        isEqualTo(actual.getBalance().doubleValue(), expectedBalance.doubleValue(), String.format("Expected balance %s but was %s", expectedBalance, actual.getBalance()));
        return this;
    }
}
