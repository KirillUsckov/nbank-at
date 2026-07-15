package ru.kduskov.api.constants;


public class ErrorMessages {
    public static class UserProfile {
        public static String NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY = "Name must contain two words with letters only";
    }

    public static class Account {
        public static final String UNAUTHORIZED_ACCESS_TO_ACCOUNT = "Unauthorized access to account";
    }

    public static class Deposit {
        public static final String INVALID_ACCOUNT_OR_AMOUNT  = "Invalid account or amount";
        public static final String DEPOSIT_AMOUNT_EXCEED_MAX = "Deposit amount exceeds the 5000 limit";
    }

    public static class Transfer {
        public static final String TRANSFER_AMOUNT_CANNOT_EXCEED_MAX = "Transfer amount cannot exceed 10000";
        public static final String INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS = "Invalid transfer: insufficient funds or invalid accounts";
    }
}
