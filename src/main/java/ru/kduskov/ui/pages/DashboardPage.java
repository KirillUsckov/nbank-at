package ru.kduskov.ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ru.kduskov.ui.elements.ButtonElement;

import static com.codeborne.selenide.Selenide.$;

public class DashboardPage extends BasePage {
    private final ButtonElement depositMoneyButton = new ButtonElement(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private final ButtonElement makeTransferButton = new ButtonElement(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    @Override
    protected String url() {
        return "/dashboard";
    }

    @Override
    protected By pageLocator() {
        return Selectors.byText("User Dashboard");
    }

    public void clickDepositMoneyButton() {
        depositMoneyButton.click();
    }

    public void clickMakeTransferButton() {
        makeTransferButton.click();
    }
}
