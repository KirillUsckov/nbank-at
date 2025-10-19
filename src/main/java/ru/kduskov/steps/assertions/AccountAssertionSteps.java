package ru.kduskov.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.AccountAssert;
import ru.kduskov.models.body.response.general.AccountResponseBody;

import java.util.List;
import java.util.Optional;

public class AccountAssertionSteps extends BaseAssertionsSteps {

    public AccountAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    public void assertBalanceWasNotChanged(List<AccountResponseBody> accountsBeforeRequest,
                                           List<AccountResponseBody> accountsAfterRequest,
                                           AccountResponseBody account) {

        assertions.assertThatAccounts(accountsAfterRequest)
                .containsAccountWithNumber(account.getAccountNumber())
                .accountWithNumber(account.getAccountNumber())
                .wasNotChangedComparedTo(
                        findAccountByNumber(accountsBeforeRequest, account.getAccountNumber())
                                .orElseThrow(() -> new AssertionError("Account not found in before request"))
                );
    }

    public void assertBalanceWasIncreased(List<AccountResponseBody> accountsBeforeRequest,
                                          List<AccountResponseBody> accountsAfterRequest,
                                          AccountResponseBody account,
                                          double transactionAmount) {
        assertAccountExistInList(accountsAfterRequest, account)
                .wasIncreasedBy(transactionAmount,
                        findAccountByNumber(accountsBeforeRequest, account.getAccountNumber())
                                .orElseThrow(() -> new AssertionError("Account not found in before request"))
                );
    }

    public void assertBalanceWasDecreased(List<AccountResponseBody> accountsBeforeRequest,
                                          List<AccountResponseBody> accountsAfterRequest,
                                          AccountResponseBody account,
                                          double transactionAmount) {

        assertions.assertThatAccounts(accountsAfterRequest)
                .containsAccountWithNumber(account.getAccountNumber())
                .accountWithNumber(account.getAccountNumber())
                .wasDencreasedBy(transactionAmount,
                        findAccountByNumber(accountsBeforeRequest, account.getAccountNumber())
                                .orElseThrow(() -> new AssertionError("Account not found in before request"))
                );
    }

    public void assertAccountHasLatestTransferOut(AccountResponseBody senderAccountAfter, double amount, Long id) {
        assertions.assertThat(senderAccountAfter).hasLatestTransferOut(amount, id);
    }

    public void assertAccountHasLatestTransferIn(AccountResponseBody receiverAccountAfter, double amount, Long id) {
        assertions.assertThat(receiverAccountAfter).hasLatestTransferIn(amount, id);
    }

    private AccountAssert assertAccountExistInList(List<AccountResponseBody> accountsAfterRequest,
                                                   AccountResponseBody account) {
        return assertions.assertThatAccounts(accountsAfterRequest)
                .containsAccountWithNumber(account.getAccountNumber())
                .accountWithNumber(account.getAccountNumber());
    }

    private Optional<AccountResponseBody> findAccountByNumber(List<AccountResponseBody> accounts, String accountNumber) {
        return accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst();
    }
}
