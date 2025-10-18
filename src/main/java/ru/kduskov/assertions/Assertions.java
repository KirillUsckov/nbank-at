package ru.kduskov.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.models.body.response.Transaction;
import ru.kduskov.models.body.response.general.UserProfileResponseBody;

import java.util.List;

public class Assertions {
    private final SoftAssertions softly;

    public Assertions(SoftAssertions softly) {
        this.softly = softly;
    }

    public AccountAssert assertThat(AccountResponseBody account) {
        return AccountAssert.assertThat(account, softly);
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
}