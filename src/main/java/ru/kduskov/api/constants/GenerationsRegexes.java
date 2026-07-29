package ru.kduskov.api.constants;

public class GenerationsRegexes {
    public static final String NAME = "^[A-Za-z]{2,50} [A-Za-z]{3,50}$";
    public static final String USERNAME = "^[a-zA-Z0-9._-]{3,15}$";
    // Финальный regex в 1 строку с lookahead для длины
//    public static final String PASSWORD = "^(?=.{8,128}$)(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@$!%+?&-])[A-Za-z0-9@$!%+?&-]+$";
}
