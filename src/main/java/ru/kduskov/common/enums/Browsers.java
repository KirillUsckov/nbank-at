package ru.kduskov.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Browsers {
    CHROME("chrome"),
    FIREFOX("firefox");
    private final String value;
}
