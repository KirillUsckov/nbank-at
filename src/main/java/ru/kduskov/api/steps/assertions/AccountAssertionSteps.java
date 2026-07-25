package ru.kduskov.api.steps.assertions;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.assertions.AccountAssert;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.response.Transaction;
import ru.kduskov.api.models.body.response.accounts.TransactionsResponseBody;
import ru.kduskov.api.models.body.response.customer.profile.CustomerAccountsResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.utils.AccountsListUtils;
import ru.kduskov.api.utils.TransactionsListUtils;

import ru.kduskov.common.steps.BaseAssertionsSteps;

import java.util.List;

public class AccountAssertionSteps extends BaseAssertionsSteps {

    public AccountAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    public void assertBalanceWasNotChanged(CustomerAccountsResponseBody accountsBeforeRequest,
                                           CustomerAccountsResponseBody accountsAfterRequest,
                                           AccountResponseBody account) {

        assertions.assertThatAccounts(accountsAfterRequest.getAccounts())
                .containsAccountWithNumber(account.getAccountNumber())
                .accountWithNumber(account.getAccountNumber())
                .matches(
                        AccountsListUtils.findAccountOrElseThrow(accountsBeforeRequest.getAccounts(), account.getAccountNumber())
                );
    }

    public void assertBalanceWasIncreased(List<AccountResponseBody> accountsBeforeRequest,
                                          List<AccountResponseBody> accountsAfterRequest,
                                          AccountResponseBody account,
                                          double transactionAmount) {
        assertAccountExistInList(accountsAfterRequest, account)
                .wasIncreasedBy(transactionAmount,
                        AccountsListUtils.findAccountOrElseThrow(accountsBeforeRequest, account.getAccountNumber())
                );
    }

    public void assertBalanceWasDecreased(List<AccountResponseBody> accountsBeforeRequest,
                                          List<AccountResponseBody> accountsAfterRequest,
                                          AccountResponseBody account,
                                          double transactionAmount) {

        assertions.assertThatAccounts(accountsAfterRequest)
                .containsAccountWithNumber(account.getAccountNumber())
                .accountWithNumber(account.getAccountNumber())
                .wasDecreasedBy(transactionAmount,
                        AccountsListUtils.findAccountOrElseThrow(accountsBeforeRequest, account.getAccountNumber())
                );
    }

    @Step("Check account has latest transaction with expected values")
    public void assertAccountHasLatestTransaction(TransactionsResponseBody senderAccountAfter, TransactionType type, double amount, Long id) {
        assertions.assertThat(senderAccountAfter).hasTransactions();
        Transaction latestTransaction = TransactionsListUtils.findLatestTransaction(senderAccountAfter.getTransactions());
        assertions.assertThat(latestTransaction).matches(amount, type, id);
    }

    @Step("Check account has no transactions with type '{type}'")
    public void assertAccountHasNoTransactionsWithType(TransactionsResponseBody account, TransactionType type) {
        assertions.assertThat(account).hasNoTransactionOfType(type);

    }

    @Step("Check account exist in list")
    private AccountAssert assertAccountExistInList(List<AccountResponseBody> accountsAfterRequest,
                                                   AccountResponseBody account) {
        return assertions.assertThatAccounts(accountsAfterRequest)
                .containsAccountWithNumber(account.getAccountNumber())
                .accountWithNumber(account.getAccountNumber());
    }
}
