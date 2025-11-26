package ui.transactions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.generators.common.RandomData;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.DepositSteps;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.api.utils.AccountsListUtils;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.ui.models.UserModel;
import ru.kduskov.ui.pages.DashboardPage;
import ru.kduskov.ui.pages.TransferPage;
import ru.kduskov.ui.steps.BrowserSteps;
import ui.BaseUiTest;

import static common.Constans.*;
import static ru.kduskov.api.enums.BankAlerts.*;

public class TransferMoneyUiTest extends BaseUiTest {
    private AccountResponseBody firstUserAccount;
    private AccountResponseBody firstUserSecondAccount;
    private UserModel firstUser;
    private AccountAssertionSteps accountAssertionSteps;
    private final DashboardPage dashboardPage = new DashboardPage();
    private final TransferPage transferPage = new TransferPage();

    public void setUpTestData() {
        firstUser = SessionStorage.getUser(FIRST_USER_ID);
        firstUserAccount = SessionStorage.getUserAccount(firstUser.getUsername(), FIRST_ACC_ID);
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        DepositSteps.sendDepositWithAmountValidation(firstUserAccount, firstUser.getToken(), 50_000);
    }

    @BeforeEach
    public void initAssertionClasses() {
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @Test
    @UserSession(accountsNumber = 2, isUi = true)
    public void transferMoneySuccessfully() {
        setUpTestData();
        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts();
        var amount = RandomData.getNumericString(4);
        var customer = userSteps.getCustomer();
        var name = customer.getName();

        var recipientAccountNumber = firstUserSecondAccount.getAccountNumber();
        dashboardPage.clickMakeTransferButton();

        transferPage.waitPageOpened();
        transferPage.selectAccount(firstUserAccount.getAccountNumber());
        transferPage.setRecipientName(name);
        transferPage.setRecipientAccountNumber(recipientAccountNumber);
        transferPage.setAmount(amount);
        transferPage.clickConfirmTransferCheckbox();
        transferPage.clickSendTransferButton();

        var successfulTransferAlertText = BrowserSteps.getAlertText();
        var expectedMessage = String.format(SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMessage(), amount, recipientAccountNumber);
        softly.assertThat(successfulTransferAlertText).isEqualTo(expectedMessage);

        var accountsAfterRequest = userSteps.getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount, Double.parseDouble(amount));
        this.accountAssertionSteps.assertBalanceWasDecreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount, Double.parseDouble(amount));

        var senderAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest, firstUserAccount);
        var receiverAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest, firstUserSecondAccount);
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderAccountAfter, Double.parseDouble(amount), firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverAccountAfter, Double.parseDouble(amount), firstUserAccount.getId());
    }

    @Test
    @UserSession(accountsNumber = 2, isUi = true)
    public void transferMoneyErrorWithoutConfirmCheckbox() {
        setUpTestData();
        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts();
        var amount = RandomData.getNumericString(4);
        var customer = userSteps.getCustomer();
        var name = customer.getName();

        var recipientAccountNumber = firstUserSecondAccount.getAccountNumber();
        dashboardPage.clickMakeTransferButton();

        transferPage.waitPageOpened();
        transferPage.selectAccount(firstUserAccount.getAccountNumber());
        transferPage.setRecipientName(name);
        transferPage.setRecipientAccountNumber(recipientAccountNumber);
        transferPage.setAmount(amount);
        transferPage.clickSendTransferButton();

        var failedTransferAlertText = BrowserSteps.getAlertText();
        softly.assertThat(failedTransferAlertText).isEqualTo(FILL_ALL_FIELDS_AND_CONFIRM.getMessage());

        var accountsAfterRequest = userSteps.getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    @UserSession(accountsNumber = 2, isUi = true)
    public void transferMoneyErrorWithMismatchedReceiverName() {
        setUpTestData();
        var requestBody = RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class);
        SessionStorage.getUserSteps(FIRST_USER_ID).changeUserProfile(requestBody);

        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts();
        var amount = RandomData.getNumericString(4);
        var name = RandomData.getValidName();

        var recipientAccountNumber = firstUserSecondAccount.getAccountNumber();

        dashboardPage.clickMakeTransferButton();

        transferPage.waitPageOpened();
        transferPage.selectAccount(firstUserAccount.getAccountNumber());
        transferPage.setRecipientName(name);
        transferPage.setRecipientAccountNumber(recipientAccountNumber);
        transferPage.setAmount(amount);
        transferPage.clickConfirmTransferCheckbox();
        transferPage.clickSendTransferButton();

        var failedTransferAlertText = BrowserSteps.getAlertText();
        softly.assertThat(failedTransferAlertText).isEqualTo(RECIPIENT_NAME_DOES_NOT_MATCH_REGISTERED_NAME.getMessage());

        var accountsAfterRequest = userSteps.getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }
}
