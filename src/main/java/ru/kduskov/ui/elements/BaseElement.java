package ru.kduskov.ui.elements;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

@AllArgsConstructor
public abstract class BaseElement {
    protected By locator;

    public void click() {
        $(locator)
                .shouldBe(Condition.visible)
                .scrollTo()
                .shouldBe(Condition.clickable)
                .click(ClickOptions.usingJavaScript());
    }

    protected SelenideElement find(By selector) {
        return $(locator).find(selector);
    }

    protected SelenideElement find(String cssSelector) {
        return $(locator).find(cssSelector);
    }

    protected ElementsCollection findAll(By selector) {
        return $(locator).findAll(selector);
    }

    protected ElementsCollection findAll(String cssSelector) {
        return $(locator).findAll(cssSelector);
    }
}
