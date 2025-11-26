package ru.kduskov.ui.elements;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

public class InputElement extends BaseElement {

    public InputElement(By locator) {
        super(locator);
    }

    public void clearAndSetValue(String value) {
        $(locator).shouldBe(Condition.enabled).click();
        $(locator).sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $(locator).sendKeys(Keys.DELETE);
        $(locator).setValue(value);
    }
}
