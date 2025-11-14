package ui.transactions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.generators.common.RandomData;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.AccountSteps;
import ru.kduskov.api.steps.DepositSteps;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.api.utils.AccountsListUtils;
import ru.kduskov.ui.pages.DashboardPage;
import ru.kduskov.ui.pages.TransferPage;

import static ru.kduskov.api.enums.BankAlerts.*;

public class TransferMoneyUiTest extends BaseTransactionUiTest {
    private static AccountResponseBody userSecondAccount;
    private AccountAssertionSteps accountAssertionSteps;
    private final DashboardPage dashboardPage = new DashboardPage();
    private final TransferPage transferPage = new TransferPage();

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

        var recipientAccountNumber = userSecondAccount.getAccountNumber();
        dashboardPage.clickMakeTransferButton();

        transferPage.waitPageOpened();
        transferPage.selectAccount(userAccount.getAccountNumber());
        transferPage.setRecipientName(name);
        transferPage.setRecipientAccountNumber(recipientAccountNumber);
        transferPage.setAmount(amount);
        transferPage.clickConfirmTransferCheckbox();
        transferPage.clickSendTransferButton();

        var successfulTransferAlertText = browserSteps.getAlertText();
        var expectedMessage = String.format(SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMessage(), amount, recipientAccountNumber);
        softly.assertThat(successfulTransferAlertText).isEqualTo(expectedMessage);

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

        var recipientAccountNumber = userSecondAccount.getAccountNumber();
        dashboardPage.clickMakeTransferButton();

        transferPage.waitPageOpened();
        transferPage.selectAccount(userAccount.getAccountNumber());
        transferPage.setRecipientName(name);
        transferPage.setRecipientAccountNumber(recipientAccountNumber);
        transferPage.setAmount(amount);
        transferPage.clickSendTransferButton();

        var failedTransferAlertText = browserSteps.getAlertText();
        softly.assertThat(failedTransferAlertText).isEqualTo(FILL_ALL_FIELDS_AND_CONFIRM.getMessage());

        var accountsAfterRequest = userSteps.getUserAccounts(userToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, userAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, userSecondAccount);
    }

    @Test
    public void transferMoneyErrorWithMismatchedReceiverName() {
        loginWithUserCredentials();
        var accountsBeforeRequest = userSteps.getUserAccounts(userToken);
        var amount = RandomData.getNumericString(4);
        var name = RandomData.getValidName();

        var recipientAccountNumber = userSecondAccount.getAccountNumber();

        dashboardPage.clickMakeTransferButton();

        transferPage.waitPageOpened();
        transferPage.selectAccount(userAccount.getAccountNumber());
        transferPage.setRecipientName(name);
        transferPage.setRecipientAccountNumber(recipientAccountNumber);
        transferPage.setAmount(amount);
        transferPage.clickConfirmTransferCheckbox();
        transferPage.clickSendTransferButton();

        var failedTransferAlertText = browserSteps.getAlertText();
        softly.assertThat(failedTransferAlertText).isEqualTo(RECIPIENT_NAME_DOES_NOT_MATCH_REGISTERED_NAME.getMessage());

        var accountsAfterRequest = userSteps.getUserAccounts(userToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, userAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, userSecondAccount);
    }
}
