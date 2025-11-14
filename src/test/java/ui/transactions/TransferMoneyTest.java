package ui.transactions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.generators.common.RandomData;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.steps.AccountSteps;
import ru.kduskov.steps.DepositSteps;
import ru.kduskov.steps.assertions.AccountAssertionSteps;
import ru.kduskov.utils.AccountsListUtils;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferMoneyTest extends BaseTransactionTest {
    private static AccountResponseBody userSecondAccount;
    private AccountAssertionSteps accountAssertionSteps;

    @BeforeAll
    public static void createAccounts() {
        DepositSteps.sendDepositWithAmountValidation(userAccount, userToken, 50_000);
        userSecondAccount = AccountSteps.createAccount(userToken);
    }

    @AfterAll
    public static void deleteAdditionalAccounts() {
        AccountSteps.deleteAccount(userToken, userSecondAccount.getId());
    }

    @BeforeEach
    public void initAssertionClasses() {
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @Test
    public void transferMoneySuccessfully() {
        loginWithUserCredentials();
        var accountsBeforeRequest = userSteps.getUserAccounts(userToken);
        var amount = RandomData.getNumericString(4);
        var customer = userSteps.getCustomer(userToken);
        var name = customer.getName();

        var receiverAccountNumber = userSecondAccount.getAccountNumber();
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(Selectors.byText("\uD83C\uDD95 New Transfer")).shouldBe(Condition.visible);

        $(Selectors.byXpath("//*[contains(@class,'account-selector')]/option[@value>0]")).shouldBe(Condition.visible);

        var availableAccounts = $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).getOptions().stream().map(SelenideElement::getText).toList();
        var targetAccount = availableAccounts.stream().filter(acc -> acc.contains(userAccount.getAccountNumber())).findFirst().get();
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).selectOption(targetAccount);
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).shouldHave(text(targetAccount));

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue(name);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(receiverAccountNumber);
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(amount);

        $(Selectors.byId("confirmCheck")).click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        var alert = Selenide.switchTo().alert();
        var successfulTransferAlertText = alert.getText();
        var expectedMessage = String.format("✅ Successfully transferred $%s to account %s!", amount, receiverAccountNumber);
        assertThat(successfulTransferAlertText).isEqualTo(expectedMessage);

        var accountsAfterRequest = userSteps.getUserAccounts(userToken);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest, accountsAfterRequest, userSecondAccount, Double.parseDouble(amount));
        this.accountAssertionSteps.assertBalanceWasDecreased(
                accountsBeforeRequest, accountsAfterRequest, userAccount, Double.parseDouble(amount));

        var senderAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest, userAccount);
        var receiverAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest, userSecondAccount);
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderAccountAfter, Double.parseDouble(amount), userSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverAccountAfter, Double.parseDouble(amount), userAccount.getId());
    }

    @Test
    public void transferMoneyErrorWithoutConfirmCheckbox() {
        loginWithUserCredentials();
        var accountsBeforeRequest = userSteps.getUserAccounts(userToken);
        var amount = RandomData.getNumericString(4);
        var customer = userSteps.getCustomer(userToken);
        var name = customer.getName();

        var receiverAccountNumber = userSecondAccount.getAccountNumber();
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(Selectors.byText("\uD83C\uDD95 New Transfer")).shouldBe(Condition.visible);

        $(Selectors.byXpath("//*[contains(@class,'account-selector')]/option[@value>0]")).shouldBe(Condition.visible);
        var availableAccounts = $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).getOptions().stream().map(SelenideElement::getText).toList();
        var targetAccount = availableAccounts.stream().filter(acc -> acc.contains(userAccount.getAccountNumber())).findFirst().get();
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).selectOption(targetAccount);
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).shouldHave(text(targetAccount));

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue(name);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(receiverAccountNumber);
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(amount);

        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        var alert = Selenide.switchTo().alert();
        var failedTransferAlertText = alert.getText();
        var expectedMessage = "❌ Please fill all fields and confirm.";
        assertThat(failedTransferAlertText).isEqualTo(expectedMessage);

        var accountsAfterRequest = userSteps.getUserAccounts(userToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, userAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, userSecondAccount);
    }

    @Test
    public void transferMoneyErrorWisMismatchedReceiverName() {
        loginWithUserCredentials();
        var accountsBeforeRequest = userSteps.getUserAccounts(userToken);
        var amount = RandomData.getNumericString(4);
        var name = RandomData.getValidName();

        var receiverAccountNumber = userSecondAccount.getAccountNumber();
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(Selectors.byText("\uD83C\uDD95 New Transfer")).shouldBe(Condition.visible);

        $(Selectors.byXpath("//*[contains(@class,'account-selector')]/option[@value>0]")).shouldBe(Condition.visible);
        var availableAccounts = $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).getOptions().stream().map(SelenideElement::getText).toList();
        var targetAccount = availableAccounts.stream().filter(acc -> acc.contains(userAccount.getAccountNumber())).findFirst().get();
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).selectOption(targetAccount);
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).shouldHave(text(targetAccount));

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue(name);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(receiverAccountNumber);
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(amount);

        $(Selectors.byId("confirmCheck")).click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        var alert = Selenide.switchTo().alert();
        var failedTransferAlertText = alert.getText();
        var expectedMessage = "❌ The recipient name does not match the registered name.";
        assertThat(failedTransferAlertText).isEqualTo(expectedMessage);

        var accountsAfterRequest = userSteps.getUserAccounts(userToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, userAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, userSecondAccount);
    }
}
