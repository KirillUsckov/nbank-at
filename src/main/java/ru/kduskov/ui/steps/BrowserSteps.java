package ru.kduskov.ui.steps;

import com.codeborne.selenide.Selenide;

public final class BrowserSteps {
    public static String getAlertText() {
        var alert = Selenide.switchTo().alert();
        return alert.getText();
    }

    public static void refresh() {
        Selenide.refresh();
    }
}
