package ru.kduskov.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ConfigParams {
    API_BASE_URL("api.baseUrl"),
    API_VERSION("api.version"),
    UI_REMOTE("ui.remote"),
    UI_BASE_URL("ui.baseUrl"),
    UI_BROWSER("ui.browser"),
    UI_BROWSER_SIZE("ui.browserSize"),
    ADMIN_USERNAME("admin.username"),
    ADMIN_PASSWORD("admin.password"),
    ADMIN_TOKEN("admin.token"),
    DB_URL("db.url"),
    DB_USERNAME("db.username"),
    DB_PASSWORD("db.password"),
    SELENOID_URL("selenoid.url"),
    SELENOID_UI_URL("selenoid.ui.url"),;
    @Getter
    private String value;
}
