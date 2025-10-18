package steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.AccountAssert;
import ru.kduskov.assertions.Assertions;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.steps.UserSteps;

import java.util.List;
import java.util.Optional;

public class AccountAssertionSteps {
    private final Assertions assertions;

    public AccountAssertionSteps(SoftAssertions softly) {
        this.assertions = new Assertions(softly);
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
                                          long depositAmount) {
        assertAccountExistInList(accountsAfterRequest, account)
                .wasIncreasedBy(depositAmount,
                        findAccountByNumber(accountsBeforeRequest, account.getAccountNumber())
                                .orElseThrow(() -> new AssertionError("Account not found in before request"))
                );
    }

    public void assertBalanceWasDecreased(List<AccountResponseBody> accountsBeforeRequest,
                                          List<AccountResponseBody> accountsAfterRequest,
                                          AccountResponseBody account,
                                          long depositAmount) {

        assertions.assertThatAccounts(accountsAfterRequest)
                .containsAccountWithNumber(account.getAccountNumber())
                .accountWithNumber(account.getAccountNumber())
                .wasDencreasedBy(depositAmount,
                        findAccountByNumber(accountsBeforeRequest, account.getAccountNumber())
                                .orElseThrow(() -> new AssertionError("Account not found in before request"))
                );
    }

    public void assertAccountHasLatestTransferOut(AccountResponseBody senderAccountAfter, int amount, Long id) {
        assertions.assertThat(senderAccountAfter).hasLatestTransferOut(amount, id);
    }

    public void assertAccountHasLatestTransferIn(AccountResponseBody receiverAccountAfter, int amount, Long id) {
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
