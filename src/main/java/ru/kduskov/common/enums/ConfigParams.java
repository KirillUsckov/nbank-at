package ru.kduskov.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ConfigParams {
    SERVER("api.server"),
    API_VERSION("api.version"),
    UI_REMOTE("ui.remote"),
    UI_BASE_URL("ui.baseUrl"),
    UI_BROWSER("ui.browser"),
    UI_BROWSER_SIZE("ui.browserSize"),
    ADMIN_USERNAME("admin.username"),
    ADMIN_PASSWORD("admin.password"),
    ADMIN_TOKEN("admin.token");
    @Getter
    private String value;
}
