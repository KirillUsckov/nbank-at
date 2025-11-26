package ru.kduskov.ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

public class SelectElement extends BaseElement{
    public SelectElement(By locator) {
        super(locator);
    }

    public void waitOptionsWithValue() {
        click();
        find(Selectors.byXpath("./option[@value>0]")).shouldBe(Condition.visible);
    }

    public List<String> getOptionsText() {
        return $(locator).getOptions().stream().map(SelenideElement::getText).collect(Collectors.toList());
    }

    public void select(String value) {
        $(locator).selectOption(value);
    }
}
