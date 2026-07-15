package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.request.DepositRequestBody;
import ru.kduskov.api.models.body.response.accounts.deposit.DepositResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

public class DepositAssert extends BaseAssert<DepositAssert, DepositResponseBody> {

    public DepositAssert(DepositResponseBody actual, SoftAssertions softly) {
        super(actual, DepositAssert.class, softly);
    }

    public static DepositAssert assertThat(DepositResponseBody actual, SoftAssertions softly) {
        return new DepositAssert(actual, softly);
    }

    public DepositAssert hasId(long expectedId) {
        softly(() ->
                softly.assertThat(actual.getId())
                        .withFailMessage("Expected account id %s but was %s", expectedId, actual.getId())
                        .isEqualTo(expectedId)
        );
        return this;
    }

    private DepositAssert hasDepositAmount(Double expectedAmount) {
        isEqualTo(
                actual.getDepositAmount(),
                expectedAmount,
                String.format("Expected depositAmount %s but was %s", expectedAmount, actual.getDepositAmount()));
        return this;
    }

    public DepositAssert hasBalance(double expectedBalance) {
        softly(() ->
                softly.assertThat(actual.getBalance())
                        .withFailMessage("Expected balance %s but was %s", expectedBalance, actual.getBalance())
                        .isEqualTo(expectedBalance)
        );
        return this;
    }

    public DepositAssert hasAccountNumber(String expectedAccountNumber) {
        softly(() ->
                softly.assertThat(actual.getAccountNumber())
                        .withFailMessage("Expected account number '%s' but was '%s'",
                                expectedAccountNumber, actual.getAccountNumber())
                        .isEqualTo(expectedAccountNumber)
        );
        return this;
    }

    private DepositAssert hasNotEmptyTransactionId() {
        softly(() ->
                softly.assertThat(actual.getTransactionId())
                        .withFailMessage("Transaction id should not be empty")
                        .isNotNull()
        );
        return this;
    }

    public DepositAssert matches(AccountResponseBody expectedAccount) {
        return hasId(expectedAccount.getId())
                .hasBalance(expectedAccount.getBalance())
                .hasAccountNumber(expectedAccount.getAccountNumber());
    }

    public DepositAssert wasNotChangedComparedTo(AccountResponseBody originalAccount) {
        return matches(originalAccount);
    }

    public DepositAssert wasIncreasedBy(double transactionAmount, AccountResponseBody originalAccount) {
        var expectedBalance = originalAccount.getBalance() + transactionAmount;
        return hasBalance(expectedBalance)
                .hasAccountNumber(originalAccount.getAccountNumber());
    }

    public DepositAssert wasDencreasedBy(double transactionAmount, AccountResponseBody originalAccount) {
        var expectedBalance = originalAccount.getBalance() - transactionAmount;
        return hasBalance(expectedBalance)
                .hasAccountNumber(originalAccount.getAccountNumber());
    }

    public DepositAssert isValidDepositResponse(DepositRequestBody depositReq, AccountResponseBody originalAccount) {
        return hasId(depositReq.getAccountId())
                .hasDepositAmount(depositReq.getAmount())
                .hasNotEmptyTransactionId()
                .hasBalance(originalAccount.getBalance())
                .hasAccountNumber(originalAccount.getAccountNumber());
    }
}