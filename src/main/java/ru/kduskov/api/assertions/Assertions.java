package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.assertions.list.AccountListAssert;
import ru.kduskov.api.models.body.response.Transaction;
import ru.kduskov.api.models.body.response.accounts.TransactionsResponseBody;
import ru.kduskov.api.models.body.response.accounts.deposit.DepositResponseBody;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.models.body.response.general.ErrorResponseBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;

import java.util.List;

public class Assertions {
    private final SoftAssertions softly;

    public Assertions(SoftAssertions softly) {
        this.softly = softly;
    }

    public AccountAssert assertThat(AccountResponseBody account) {
        return AccountAssert.assertThat(account, softly);
    }

    public DepositAssert assertThat(DepositResponseBody account) {
        return DepositAssert.assertThat(account, softly);
    }

    public TransactionsAssert assertThat(TransactionsResponseBody transaction) {
        return TransactionsAssert.assertThat(transaction, softly);
    }

    public TransactionAssert assertThat(Transaction transaction) {
        return TransactionAssert.assertThat(transaction, softly);
    }

    public TransferAssert assertThat(TransferResponseBody transfer) {
        return TransferAssert.assertThat(transfer, softly);
    }

    public AccountListAssert assertThatAccounts(List<AccountResponseBody> accounts) {
        return AccountListAssert.assertThat(accounts, softly);
    }

    public ChangeUserProfileAssert assertThat(ChangeUserProfileResponseBody userProfile) {
        return ChangeUserProfileAssert.assertThat(userProfile, softly);
    }

    public UserProfileAssert assertThat(UserProfileResponseBody userProfile) {
        return UserProfileAssert.assertThat(userProfile, softly);
    }

    public StringAssert assertThat(String string) {
        return StringAssert.assertThat(string, softly);
    }

    public ErrorResponseAssert assertThat(ErrorResponseBody error) {
        return ErrorResponseAssert.assertThat(error, softly);
    }
}