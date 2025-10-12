package ru.kduskov.utils;

import ru.kduskov.models.body.response.Account;

import java.util.List;

public final class AccountsListUtils {
    public static Account findAccountByAccountNumberOrElseThrow(
            List<Account> accounts,
            Account expectedAccount) {
        return accounts.stream().filter(acc -> acc.getAccountNumber().equals(expectedAccount.getAccountNumber())).findFirst().orElseThrow();
    }
}
