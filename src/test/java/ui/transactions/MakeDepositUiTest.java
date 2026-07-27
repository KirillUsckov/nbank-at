package ui.transactions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.generators.DepositRequestGenerator;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.steps.StringAssertionsSteps;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.ui.enums.MessageTypes;
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
    private final DashboardPage dashboardPage = new DashboardPage();
    private final MakeDepositPage makeDepositPage = new MakeDepositPage();
    private StringAssertionsSteps stringAssertionsSteps;
    private AccountAssertionSteps accountAssertionSteps;

    @BeforeEach
    public void initAssertionClasses() {
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
        this.stringAssertionsSteps = new StringAssertionsSteps(softly);
    }

    @Test
    @UserSession(accountsNumber = 1, isUi = true)
    public void makeDepositSuccessfully() {
        var user = SessionStorage.getUser(FIRST_USER_ID);
        var userAccount = SessionStorage.getUserAccount(user.getUsername(), FIRST_ACC_ID);

        loginWithUserCredentials(user.getToken());
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts().getAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(userAccount.getId());

        dashboardPage.waitPageOpened();
        dashboardPage.clickDepositMoneyButton();

        makeDepositPage.waitPageOpened();

        makeDepositPage.selectAccount(userAccount.getAccountNumber());
        makeDepositPage.setAmount(String.valueOf(depositRequestBody.getAmount()));
        makeDepositPage.clickDepositButton();

        var successfulMessage = BrowserSteps.getAlertText();
        var expectedMessage = String.format(
                SUCCESSFULLY_DEPOSITED_TO_ACCOUNT.getMessage(),
                depositRequestBody.getAmount(),
                userAccount.getAccountNumber()
        );
        stringAssertionsSteps.assertTextEqualsTo(MessageTypes.ALERT.getTxt(), expectedMessage, successfulMessage);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts().getAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                userAccount,
                depositRequestBody.getAmount());
    }

    @Test
    @UserSession(accountsNumber = 1, isUi = true)
    public void makeDepositErrorWithTooHighAmount() {
        var user = SessionStorage.getUser(FIRST_USER_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var userAccount = SessionStorage.getUserAccount(user.getUsername(), FIRST_ACC_ID);

        dashboardPage.waitPageOpened();
        dashboardPage.clickDepositMoneyButton();
        makeDepositPage.waitPageOpened();
        makeDepositPage.selectAccount(userAccount.getAccountNumber());
        makeDepositPage.setAmount("5000.01");
        makeDepositPage.clickDepositButton();

        var errorMessage = BrowserSteps.getAlertText();
        stringAssertionsSteps.assertTextEqualsTo(MessageTypes.ALERT.getTxt(), DEPOSIT_LESS_OR_EQUAL_TO_5000.getMessage(), errorMessage);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, userAccount);
    }
}
