package ru.kduskov.ui.elements;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ButtonElement extends BaseElement {
    public ButtonElement(By locator) {
        super(locator);
    }

    public void click(){
        $(locator).click();
    }
}
