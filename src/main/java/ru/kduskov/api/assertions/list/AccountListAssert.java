package ru.kduskov.api.assertions.list;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.assertions.AccountAssert;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.util.List;
import java.util.Optional;

public class AccountListAssert extends BaseListAssert<AccountListAssert, AccountResponseBody> {

    public AccountListAssert(List<AccountResponseBody> actual, SoftAssertions softly) {
        super(actual, AccountListAssert.class, softly);
    }

    public static AccountListAssert assertThat(List<AccountResponseBody> actual, SoftAssertions softly) {
        return new AccountListAssert(actual, softly);
    }

    public AccountListAssert containsAccountWithNumber(String accountNumber) {
        softly(() -> {
            Optional<AccountResponseBody> account = actual.stream()
                    .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                    .findFirst();

            softly.assertThat(account)
                    .withFailMessage("Account with number '%s' not found", accountNumber)
                    .isPresent();
        });
        return this;
    }

    public AccountAssert accountWithNumber(String accountNumber) {
        Optional<AccountResponseBody> account = actual.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst();

        softly.assertThat(account)
                .withFailMessage("Account with number '%s' not found", accountNumber)
                .isPresent();

        return new AccountAssert(account.get(), softly);
    }
}