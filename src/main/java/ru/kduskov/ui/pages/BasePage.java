package ru.kduskov.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage> {
    abstract String url();
    abstract By pageLocator();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }
    public <T extends BasePage> T goTo(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public void waitPageOpened() {
        $(pageLocator()).shouldBe(Condition.visible);
    }
}
