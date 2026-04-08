package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

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

    public AccountAssert isValidDepositResponse(long accountId, AccountResponseBody originalAccount) {
        return hasId(accountId)
                .hasBalance(originalAccount.getBalance())
                .hasAccountNumber(originalAccount.getAccountNumber());
    }
}