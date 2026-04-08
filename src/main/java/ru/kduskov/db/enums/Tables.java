package ru.kduskov.db.enums;

import lombok.Getter;

public enum Tables {
    CUSTOMERS("customers"),
    ACCOUNTS("accounts"),
    USERS("users");
    @Getter
    private final String tableName;

    Tables(String tableName) {
        this.tableName = tableName;
    }
}
