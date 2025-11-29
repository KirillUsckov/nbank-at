package ru.kduskov.ui.elements;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ButtonElement extends BaseElement {
    public ButtonElement(By locator) {
        super(locator);
    }
}
