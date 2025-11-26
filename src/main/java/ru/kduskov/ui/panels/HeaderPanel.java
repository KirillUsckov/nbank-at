package ru.kduskov.ui.panels;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class HeaderPanel {
    private final SelenideElement userInfoLabel = $(Selectors.byClassName("user-info"));
    private final SelenideElement userNameLabel = $(Selectors.byXpath("//div[@class='user-info']//span[@class='user-name']"));
    public void clickUserInfo() {
        userInfoLabel.shouldBe(Condition.visible).click();
    }

    public String getUserNameFromUserInfo() {
        return userNameLabel.getText();
    }
}
