package ru.kduskov.ui.enums;

import lombok.Getter;

public enum MessageTypes {
    ALERT("Alert text"),
    HEADER_USERNAME("Username in header");
    @Getter private String txt;
    MessageTypes(String txt) {
        this.txt = txt;
    }
}
