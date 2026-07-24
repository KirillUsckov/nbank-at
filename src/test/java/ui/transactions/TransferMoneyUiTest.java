package ui.transactions;

import io.qameta.allure.Step;
import ru.kduskov.api.enums.TransactionType;
import support.TransactionTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.generators.common.RandomData;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.ui.pages.DashboardPage;
import ru.kduskov.ui.pages.TransferPage;
import ru.kduskov.ui.steps.BrowserSteps;
import ui.BaseUiTest;

import static common.Constans.FIRST_ACC_ID;
import static common.Constans.FIRST_USER_ID;
import static common.Constans.SECOND_USER_ID;
import static ru.kduskov.api.enums.BankAlerts.FILL_ALL_FIELDS_AND_CONFIRM;
import static ru.kduskov.api.enums.BankAlerts.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT;
import static ru.kduskov.api.enums.BankAlerts.RECIPIENT_NAME_DOES_NOT_MATCH_REGISTERED_NAME;

public class TransferMoneyUiTest extends BaseUiTest {
    private AccountResponseBody firstUserAccount;
    private AccountResponseBody firstUserSecondAccount;
    private AccountAssertionSteps accountAssertionSteps;
    private final DashboardPage dashboardPage = new DashboardPage();
    private final TransferPage transferPage = new TransferPage();


    @Step("Prepare funded sender account")
    public void prepareFundedSenderAccount() {
        var testData = TransactionTestData.getAccountWithDeposit(FIRST_USER_ID, FIRST_ACC_ID, 50_000);
        firstUserAccount = testData.getAccount();
        firstUserSecondAccount = TransactionTestData.getUserAccount(FIRST_USER_ID, SECOND_USER_ID).getAccount();
    }

    @BeforeEach
    public void initAssertionClasses() {
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @Test
    @UserSession(accountsNumber = 2, isUi = true)
    public void transferMoneySuccessfully() {
        prepareFundedSenderAccount();
        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts().getAccounts();
        var amount = RandomData.getNumericString(4);
        var customer = userSteps.getUserProfile();
        var name = customer.getName();

        var recipientAccountNumber = firstUserSecondAccount.getAccountNumber();
        dashboardPage.clickMakeTransferButton();
        BrowserSteps.refresh();

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

        var accountsAfterRequest = userSteps.getUserAccounts().getAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount, Double.parseDouble(amount));
        this.accountAssertionSteps.assertBalanceWasDecreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount, Double.parseDouble(amount));

        var senderTransactions = userSteps.getAccountTransactions(firstUserAccount.getId());
        var receiverTransactions = userSteps.getAccountTransactions(firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransaction(
                senderTransactions,
                TransactionType.TRANSFER_OUT,
                Double.parseDouble(amount),
                firstUserSecondAccount.getId()
        );
        this.accountAssertionSteps.assertAccountHasLatestTransaction(
                receiverTransactions,
                TransactionType.TRANSFER_IN,
                Double.parseDouble(amount),
                firstUserAccount.getId()
        );
    }

    @Test
    @UserSession(accountsNumber = 2, isUi = true)
    public void transferMoneyErrorWithoutConfirmCheckbox() {
        prepareFundedSenderAccount();
        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts();
        var amount = RandomData.getNumericString(4);
        var customer = userSteps.getUserProfile();
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
        prepareFundedSenderAccount();

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
