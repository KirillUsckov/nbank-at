package ru.kduskov.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

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

    public void refresh() {
        Selenide.refresh();
    }

    public static void loginWithUserCredentials(String userToken) {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", userToken);
        new DashboardPage()
                .open()
                .waitPageOpened();
    }
}
