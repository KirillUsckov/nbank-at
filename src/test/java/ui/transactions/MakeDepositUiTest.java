package ui.transactions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.generators.DepositRequestGenerator;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.ui.pages.DashboardPage;
import ru.kduskov.ui.pages.MakeDepositPage;
import ru.kduskov.ui.steps.BrowserSteps;
import ui.BaseUiTest;

import static common.Constans.FIRST_ACC_ID;
import static common.Constans.FIRST_USER_ID;
import static ru.kduskov.api.enums.BankAlerts.DEPOSIT_LESS_OR_EQUAL_TO_5000;
import static ru.kduskov.api.enums.BankAlerts.SUCCESSFULLY_DEPOSITED_TO_ACCOUNT;
import static ru.kduskov.ui.pages.BasePage.loginWithUserCredentials;

public class MakeDepositUiTest extends BaseUiTest {
    private AccountAssertionSteps accountAssertionSteps;
    private final DashboardPage dashboardPage = new DashboardPage();
    private final MakeDepositPage makeDepositPage = new MakeDepositPage();

    @BeforeEach
    public void initAssertionClasses() {
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @Test
    @UserSession(accountsNumber = 1, isUi = true)
    public void makeDepositSuccessfully() {
        var user = SessionStorage.getUser(FIRST_USER_ID);
        var userAccount = SessionStorage.getUserAccount(user.getUsername(), FIRST_ACC_ID);

        loginWithUserCredentials(user.getToken());
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(userAccount.getId());
        dashboardPage.clickDepositMoneyButton();

        makeDepositPage.waitPageOpened();

        makeDepositPage.selectAccount(userAccount.getAccountNumber());
        makeDepositPage.setAmount(String.valueOf(depositRequestBody.getBalance()));
        makeDepositPage.clickDepositButton();

        var successfulMessage = BrowserSteps.getAlertText();
        var expectedMessage = String.format(SUCCESSFULLY_DEPOSITED_TO_ACCOUNT.getMessage(), depositRequestBody.getBalance(), userAccount.getAccountNumber());
        softly.assertThat(successfulMessage).withFailMessage("Alert message is not equal expected").isEqualTo(expectedMessage);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                userAccount,
                depositRequestBody.getBalance());

    }

    @Test
    @UserSession(accountsNumber = 1, isUi = true)
    public void makeDepositErrorWithTooHighAmount() {
        var user = SessionStorage.getUser(FIRST_USER_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var userAccount = SessionStorage.getUserAccount(user.getUsername(), FIRST_ACC_ID);

        dashboardPage.clickDepositMoneyButton();
        makeDepositPage.waitPageOpened();
        makeDepositPage.selectAccount(userAccount.getAccountNumber());
        makeDepositPage.setAmount("5000.01");
        makeDepositPage.clickDepositButton();

        var errorMessage = BrowserSteps.getAlertText();
        softly.assertThat(errorMessage).isEqualTo(DEPOSIT_LESS_OR_EQUAL_TO_5000.getMessage());

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, userAccount);
    }
}
