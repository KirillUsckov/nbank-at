package ru.kduskov.ui.pages;

import com.codeborne.selenide.Selectors;
import org.openqa.selenium.By;
import ru.kduskov.ui.elements.ButtonElement;
import ru.kduskov.ui.elements.InputElement;
import ru.kduskov.ui.elements.SelectElement;

public class TransferPage extends BasePage {
    private final SelectElement accountsSelect = new SelectElement(Selectors.byXpath("//*[contains(@class,'account-selector')]"));

    private final InputElement amountInput = new InputElement(Selectors.byAttribute("placeholder", "Enter amount"));

    private final InputElement recipientNameInput = new InputElement(Selectors.byAttribute("placeholder", "Enter recipient name"));

    private final InputElement recipientAccountNumberInput = new InputElement(Selectors.byAttribute("placeholder", "Enter recipient account number"));

    private final ButtonElement sendTransferButton = new ButtonElement(Selectors.byXpath("//button[contains(text(), 'Send Transfer')]"));

    private final ButtonElement confirmTransferCheckbox = new ButtonElement(Selectors.byId("confirmCheck"));

    @Override
    String url() {
        return "/transfer";
    }

    @Override
    By pageLocator() {
        return Selectors.byXpath("//button[contains(text(), 'Send Transfer')]");
    }

    public void selectAccount(String accountNumber) {
        accountsSelect.waitOptionsWithValue();
        var availableAccounts =  accountsSelect.getOptionsText();
        var accountPart = String.format("%s (Balance:", accountNumber);
        var targetAccount = availableAccounts.stream().filter(acc -> acc.contains(accountPart)).findFirst().orElseThrow();
        accountsSelect.select(targetAccount);
    }

    public void setAmount(String amount) {
        amountInput.sendKeys(amount);
    }

    public void setRecipientAccountNumber(String accountNumber) {
        recipientAccountNumberInput.sendKeys(accountNumber);
    }

    public void setRecipientName(String recipientName) {
        recipientNameInput.sendKeys(recipientName);
    }

    public void clickSendTransferButton() {
        sendTransferButton.click();
    }

    public void clickConfirmTransferCheckbox() {
        confirmTransferCheckbox.click();
    }
}
