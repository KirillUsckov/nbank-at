package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.util.List;
import java.util.Optional;

public class AccountListAssert extends BaseAssert<AccountListAssert, List<AccountResponseBody>> {

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

    public AccountListAssert hasSameSizeAs(List<AccountResponseBody> otherList) {
        softly(() ->
                softly.assertThat(actual)
                        .withFailMessage("Expected %s accounts but found %s",
                                otherList.size(), actual.size())
                        .hasSameSizeAs(otherList)
        );
        return this;
    }
}