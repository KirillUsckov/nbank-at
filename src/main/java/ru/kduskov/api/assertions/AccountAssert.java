package ru.kduskov.api.assertions;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.common.assertions.BaseAssert;

public class AccountAssert extends BaseAssert<AccountAssert, AccountResponseBody> {
    public AccountAssert(AccountResponseBody actual, SoftAssertions softly) {
        super(actual, AccountAssert.class, softly);
    }

    public static AccountAssert assertThat(AccountResponseBody actual, SoftAssertions softly) {
        return new AccountAssert(actual, softly);
    }

    @Step("Check id is {expectedId}")
    public AccountAssert hasId(long expectedId) {
        softly(() ->
                softly.assertThat(actual.getId())
                        .withFailMessage("Expected account id %s but was %s", expectedId, actual.getId())
                        .isEqualTo(expectedId)
        );
        return this;
    }

    @Step("Check balance is {expectedBalance}")
    public AccountAssert hasBalance(double expectedBalance) {
        softly(() ->
                softly.assertThat(actual.getBalance())
                        .withFailMessage("Expected balance %s but was %s", expectedBalance, actual.getBalance())
                        .isEqualTo(expectedBalance)
        );
        return this;
    }

    @Step("Check account number is {expectedAccountNumber}")
    public AccountAssert hasAccountNumber(String expectedAccountNumber) {
        softly(() ->
                softly.assertThat(actual.getAccountNumber())
                        .withFailMessage("Expected account number '%s' but was '%s'",
                                expectedAccountNumber, actual.getAccountNumber())
                        .isEqualTo(expectedAccountNumber)
        );
        return this;
    }

    @Step("Check AccountResponse matches expected {expectedAccount}")
    public AccountAssert matches(AccountResponseBody expectedAccount) {
        return hasId(expectedAccount.getId())
                .hasBalance(expectedAccount.getBalance())
                .hasAccountNumber(expectedAccount.getAccountNumber());
    }

    @Step("Check Account balance was increased by {transactionAmount}")
    public AccountAssert wasIncreasedBy(double transactionAmount, AccountResponseBody originalAccount) {
        var expectedBalance = originalAccount.getBalance() + transactionAmount;
        return hasBalance(expectedBalance)
                .hasAccountNumber(originalAccount.getAccountNumber());
    }

    @Step("Check Account balance was decreased by {transactionAmount}")
    public AccountAssert wasDecreasedBy(double transactionAmount, AccountResponseBody originalAccount) {
        var expectedBalance = originalAccount.getBalance() - transactionAmount;
        return hasBalance(expectedBalance)
                .hasAccountNumber(originalAccount.getAccountNumber());
    }
}