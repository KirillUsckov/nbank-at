package ru.kduskov.api.assertions.list;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.db.assertions.AccountDaoAssert;
import ru.kduskov.db.models.dao.AccountDao;

import java.util.List;
import java.util.Optional;

public class AccountDaoListAssert extends BaseListAssert<AccountDaoListAssert, AccountDao> {
    public AccountDaoListAssert(List<AccountDao> actual, SoftAssertions softly) {
        super(actual, AccountDaoListAssert.class, softly);
    }

    public static AccountDaoListAssert assertThat(List<AccountDao> actual, SoftAssertions softly) {
        return new AccountDaoListAssert(actual, softly);
    }

    public AccountDaoListAssert equals(List<AccountDao> expected) {
        sizeEquals(expected.size());
        for (int i = 0; i < expected.size(); i++) {
            var actualValue = actual.get(i);
            var expectedValue = expected.get(i);
            new AccountDaoAssert(actualValue, softly)
                    .equals(expectedValue);
        }
        return this;
    }

    public AccountDaoListAssert containsAccountWithNumber(String accountNumber) {
        softly(() -> {
            Optional<AccountDao> account = actual.stream()
                    .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                    .findFirst();

            softly.assertThat(account)
                    .withFailMessage("Account with number '%s' not found", accountNumber)
                    .isPresent();
        });
        return this;
    }

    public AccountDaoAssert accountWithNumber(String accountNumber) {
        Optional<AccountDao> account = actual.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst();

        softly.assertThat(account)
                .withFailMessage("Account with number '%s' not found", accountNumber)
                .isPresent();

        return new AccountDaoAssert(account.get(), softly);
    }
}