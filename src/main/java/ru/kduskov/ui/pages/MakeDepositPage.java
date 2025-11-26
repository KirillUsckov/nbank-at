package ru.kduskov.ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ru.kduskov.ui.elements.ButtonElement;
import ru.kduskov.ui.elements.InputElement;
import ru.kduskov.ui.elements.SelectElement;

import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MakeDepositPage extends BasePage {
    private final ButtonElement depositButton = new ButtonElement(Selectors.byText("\uD83D\uDCB5 Deposit"));
    private final SelectElement accountsSelect = new SelectElement(Selectors.byXpath("//*[contains(@class,'account-selector')]"));
    private final InputElement amountInput = new InputElement(Selectors.byAttribute("placeholder","Enter amount"));

    @Override
    protected String url() {
        return "/deposit";
    }

    @Override
    protected By pageLocator() {
        return Selectors.byText("\uD83D\uDCB5 Deposit");
    }

    public void clickDepositButton() {
        depositButton.click();
    }

    public void selectAccount(String accountNumber) {
        accountsSelect.waitOptionsWithValue();
        var availableAccounts =  accountsSelect.getOptionsText();
        var accountPart = String.format("%s (Balance:", accountNumber);
        var targetAccount = availableAccounts.stream().filter(acc->acc.contains(accountPart)).findFirst().orElseThrow();
        accountsSelect.select(targetAccount);
    }

    public void setAmount(String amount) {
        amountInput.clearAndSetValue(amount);
    }
}
