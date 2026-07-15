package ru.kduskov.ui.pages;

import com.codeborne.selenide.Selectors;
import org.openqa.selenium.By;
import ru.kduskov.ui.elements.ButtonElement;
import ru.kduskov.ui.elements.InputElement;
import ru.kduskov.ui.elements.SelectElement;

public class MakeDepositPage extends BasePage {
    private final String DEPOSIT_LABEL = "💵 Deposit";

    private final ButtonElement depositButton = new ButtonElement(Selectors.byText(DEPOSIT_LABEL));

    private final SelectElement accountsSelect = new SelectElement(Selectors.byXpath("//*[contains(@class,'account-selector')]"));

    private final InputElement amountInput = new InputElement(Selectors.byAttribute("placeholder","Enter amount"));

    @Override
    protected String url() {
        return "/deposit";
    }

    @Override
    protected By pageLocator() {
        return Selectors.byText(DEPOSIT_LABEL);
    }

    public void clickDepositButton() {
        depositButton.click();
    }

    public void selectAccount(String accountNumber) {
        accountsSelect.waitOptionsWithValue();
        var availableAccounts =  accountsSelect.getOptionsText();
        var accountPart = String.format("%s (Balance:", accountNumber);
        var targetAccount = availableAccounts.stream().filter(acc -> acc.contains(accountPart)).findFirst().orElseThrow();
        accountsSelect.select(targetAccount);
    }

    public void setAmount(String amount) {
        amountInput.clearAndSetValue(amount);
    }
}
