package ru.kduskov.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ConfigParams {
    SERVER("server"),
    API_VERSION("apiVersion");
    @Getter
    private String value;
}
