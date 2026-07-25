package ru.kduskov.ui.elements;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class InputElement extends BaseElement {

    public InputElement(By locator) {
        super(locator);
    }

    public void setValue(String value) {
        $(locator)
                .shouldBe(Condition.visible, Condition.enabled)
                .setValue(value);
    }
    public void sendKeys(String value) {
        $(locator)
                .shouldBe(Condition.visible, Condition.enabled)
                .sendKeys(value);
    }
    public void append(String value) {
        $(locator)
                .shouldBe(Condition.visible, Condition.enabled)
                .append(value);
    }
}
