package ui.transactions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.generators.DepositRequestGenerator;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.ui.pages.DashboardPage;
import ru.kduskov.ui.pages.MakeDepositPage;
import static ru.kduskov.api.enums.BankAlerts.DEPOSIT_LESS_OR_EQUAL_TO_5000;
import static ru.kduskov.api.enums.BankAlerts.SUCCESSFULLY_DEPOSITED_TO_ACCOUNT;

public class MakeDepositUiTest extends BaseTransactionUiTest {
    private AccountAssertionSteps accountAssertionSteps;
    private final DashboardPage dashboardPage = new DashboardPage();
    private final MakeDepositPage makeDepositPage = new MakeDepositPage();

    @BeforeEach
    public void initAssertionClasses() {
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @Test
    public void makeDepositSuccessfully() {
        loginWithUserCredentials();
        var accountsBeforeRequest = userSteps.getUserAccounts(userToken);
        var depositRequestBody = DepositRequestGenerator.generate(userAccount.getId());
        dashboardPage.clickDepositMoneyButton();
        makeDepositPage.waitPageOpened();

        makeDepositPage.selectAccount(userAccount.getAccountNumber());
        makeDepositPage.setAmount(String.valueOf(depositRequestBody.getBalance()));
        makeDepositPage.clickDepositButton();

        var successfulMessage = browserSteps.getAlertText();
        var expectedMessage = String.format(SUCCESSFULLY_DEPOSITED_TO_ACCOUNT.getMessage(), depositRequestBody.getBalance(), userAccount.getAccountNumber());
        softly.assertThat(successfulMessage).withFailMessage("Alert message is not equal expected").isEqualTo(expectedMessage);

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

        dashboardPage.clickDepositMoneyButton();
        makeDepositPage.waitPageOpened();

        makeDepositPage.selectAccount(userAccount.getAccountNumber());
        makeDepositPage.setAmount("5001");
        makeDepositPage.clickDepositButton();

        var errorMessage = browserSteps.getAlertText();
        softly.assertThat(errorMessage).isEqualTo(DEPOSIT_LESS_OR_EQUAL_TO_5000.getMessage());

        var accountsAfterRequest = userSteps.getUserAccounts(userToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, userAccount);
    }
}
