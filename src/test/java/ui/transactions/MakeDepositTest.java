package ui.transactions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.generators.DepositRequestGenerator;
import ru.kduskov.steps.assertions.AccountAssertionSteps;

import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

public class MakeDepositTest extends BaseTransactionTest {
    private AccountAssertionSteps accountAssertionSteps;

    @BeforeEach
    public void initAssertionClasses() {
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @Test
    public void makeDepositSuccessfully() {
        loginWithUserCredentials();
        var accountsBeforeRequest = userSteps.getUserAccounts(userToken);

        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).shouldBe(Condition.visible);
        var depositRequestBody = DepositRequestGenerator.generate(userAccount.getId());

        $(Selectors.byXpath("//*[contains(@class,'account-selector')]/option[@value>0]")).shouldBe(Condition.visible);
        var availableAccounts =  $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).getOptions().stream().map(el -> el.getText()).collect(Collectors.toList());
        var targetAccount = availableAccounts.stream().filter(acc->acc.contains(userAccount.getAccountNumber())).findFirst().get();
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).selectOption(targetAccount);
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).shouldHave(text(targetAccount));
        $(Selectors.byAttribute("placeholder","Enter amount")).setValue(String.valueOf(depositRequestBody.getBalance()));

        $(Selectors.byText("\uD83D\uDCB5 Deposit")).click();
        var successfulDepositAlert = Selenide.switchTo().alert();
        var successfulMessage = successfulDepositAlert.getText();
        var expectedMessage = String.format("✅ Successfully deposited $%s to account %s!", depositRequestBody.getBalance(), userAccount.getAccountNumber());
        assertThat(successfulMessage).isEqualTo(expectedMessage);

        var accountsAfterRequest = userSteps.getUserAccounts(userToken);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                userAccount,
                depositRequestBody.getBalance());

    }

    @Test
    public void makeDepositErrorWithTooHighAmount() {
        loginWithUserCredentials();
        var accountsBeforeRequest = userSteps.getUserAccounts(userToken);

        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).shouldBe(Condition.visible);

        $(Selectors.byXpath("//*[contains(@class,'account-selector')]/option[@value>0]")).shouldBe(Condition.visible);
        var availableAccounts =  $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).getOptions().stream().map(el -> el.getText()).collect(Collectors.toList());
        var targetAccount = availableAccounts.stream().filter(acc->acc.contains(userAccount.getAccountNumber())).findFirst().get();
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).selectOption(targetAccount);
        $(Selectors.byXpath("//*[contains(@class,'account-selector')]")).shouldHave(text(targetAccount));
        $(Selectors.byAttribute("placeholder","Enter amount")).setValue("5001");

        $(Selectors.byText("\uD83D\uDCB5 Deposit")).click();
        var errorDepositAlert = Selenide.switchTo().alert();
        var errorMessage = errorDepositAlert.getText();
        assertThat(errorMessage).isEqualTo("❌ Please deposit less or equal to 5000$.");

        var accountsAfterRequest = userSteps.getUserAccounts(userToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, userAccount);
    }
}
