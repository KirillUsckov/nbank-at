package ru.kduskov.ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class DashboardPage extends BasePage {
    private final SelenideElement depositMoneyButton = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private final SelenideElement makeTransferButton = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
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
