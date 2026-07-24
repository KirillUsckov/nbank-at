package ru.kduskov.common.utils;

public final class LogMaskingUtils {

    private LogMaskingUtils() {
    }

    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        int visibleCharacters = 4;

        if (accountNumber.length() <= visibleCharacters) {
            return accountNumber;
        }

        return "*".repeat(accountNumber.length() - visibleCharacters)
                + accountNumber.substring(accountNumber.length() - visibleCharacters);
    }
}
