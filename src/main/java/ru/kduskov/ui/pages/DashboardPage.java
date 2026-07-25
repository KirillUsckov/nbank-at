package ru.kduskov.ui.pages;

import com.codeborne.selenide.Selectors;
import org.openqa.selenium.By;
import ru.kduskov.ui.elements.ButtonElement;

public class DashboardPage extends BasePage {
    private final ButtonElement depositMoneyButton = new ButtonElement(Selectors.byXpath("//button[contains(text(),'Deposit Money')]"));

    private final ButtonElement makeTransferButton = new ButtonElement(Selectors.byXpath("//button[contains(text(),'Make a Transfer')]"));

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
