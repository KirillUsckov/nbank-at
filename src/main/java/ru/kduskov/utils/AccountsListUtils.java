package ru.kduskov.utils;

import ru.kduskov.models.body.response.general.AccountResponseBody;

import java.util.List;

public final class AccountsListUtils {
    public static AccountResponseBody findAccountByAccountNumberOrElseThrow(
            List<AccountResponseBody> accounts,
            AccountResponseBody expectedAccount) {
        return accounts.stream().filter(acc -> acc.getAccountNumber().equals(expectedAccount.getAccountNumber())).findFirst().orElseThrow();
    }
}
