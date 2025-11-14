package ru.kduskov.ui.elements;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;

@AllArgsConstructor
public abstract class BaseElement {
    protected By locator;

}
