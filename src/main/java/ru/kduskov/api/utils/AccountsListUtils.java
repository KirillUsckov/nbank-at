package ru.kduskov.api.utils;

import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.util.List;

public final class AccountsListUtils {
    private AccountsListUtils() {}

    public static AccountResponseBody findAccountOrElseThrow(
            List<AccountResponseBody> accounts,
            String accountNumber
    ) {
        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Account with number '%s' not found"
                                .formatted(accountNumber)
                ));
    }
}
