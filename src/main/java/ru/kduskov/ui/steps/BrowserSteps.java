package ru.kduskov.ui.steps;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

public final class BrowserSteps {
    @Step("Get alert text")
    public static String getAlertText() {
        var alert = Selenide.switchTo().alert();
        return alert.getText();
    }

    @Step("Refresh")
    public static void refresh() {
        Selenide.refresh();
    }
}
