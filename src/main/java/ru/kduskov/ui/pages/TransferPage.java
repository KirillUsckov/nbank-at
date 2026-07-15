package ru.kduskov.ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ru.kduskov.ui.elements.InputElement;
import ru.kduskov.ui.elements.SelectElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage extends BasePage {
    private final SelectElement accountsSelect = new SelectElement(Selectors.byXpath("//*[contains(@class,'account-selector')]"));

    private final InputElement amountInput = new InputElement(Selectors.byAttribute("placeholder", "Enter amount"));

    private final InputElement recipientNameInput = new InputElement(Selectors.byAttribute("placeholder", "Enter recipient name"));

    private final InputElement recipientAccountNumberInput = new InputElement(Selectors.byAttribute("placeholder", "Enter recipient account number"));

    private final SelenideElement sendTransferButton = $(Selectors.byText("🚀 Send Transfer"));

    private final SelenideElement confirmTransferCheckbox = $(Selectors.byId("confirmCheck"));

    @Override
    String url() {
        return "/transfer";
    }

    @Override
    By pageLocator() {
        return Selectors.byText("🆕 New Transfer");
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

    public void setRecipientAccountNumber(String accountNumber) {
        recipientAccountNumberInput.clearAndSetValue(accountNumber);
    }

    public void setRecipientName(String recipientName) {
        recipientNameInput.clearAndSetValue(recipientName);
    }

    public void clickSendTransferButton() {
        sendTransferButton.click();
    }

    public void clickConfirmTransferCheckbox() {
        confirmTransferCheckbox.click();
    }
}
