package ru.kduskov.api.utils;

import lombok.experimental.UtilityClass;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.util.List;

@UtilityClass
public final class AccountsListUtils {
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
