package ru.kduskov.ui.steps;

import com.codeborne.selenide.Selenide;

public class BrowserSteps {
    public String getAlertText() {
        var alert = Selenide.switchTo().alert();
        return alert.getText();
    }

    public void refresh() {
        Selenide.refresh();
    }
}
