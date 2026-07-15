package api.transactions;

import support.ExpectedAccountState;
import support.TransactionTestData;
import support.TransferDbAssertions;
import common.BaseMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.api.models.body.request.TransferRequestBody;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.models.body.response.customer.profile.CustomerAccountsResponseBody;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.mock.annotations.FraudMockStatus;
import ru.kduskov.mock.enums.FraudStatus;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.generators.TransferRequestGenerator;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.api.steps.TransferSteps;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.api.steps.assertions.TransferAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.db.steps.DbAssertionSteps;
import ru.kduskov.db.steps.SqlSteps;
import ru.kduskov.ui.models.UserModel;

import static common.Constans.FIRST_USER_ID;
import static common.Constans.FIRST_ACC_ID;
import static common.Constans.SECOND_ACC_ID;

public class TransferMoneyWithFraudApiTest extends BaseMockTest {
    private AccountResponseBody firstUserAccount;
    private AccountResponseBody firstUserSecondAccount;
    private UserModel firstUser;
    private TransferAssertionSteps transferAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;
    private DbAssertionSteps dbAssertionSteps;

    public void setUpTestData() {
        var testData = TransactionTestData.getAccountWithDeposit(FIRST_USER_ID, FIRST_ACC_ID, 50_000);
        firstUser = testData.getUser();
        firstUserAccount = testData.getAccount();
    }

    @BeforeEach
    public void initAssertionClasses() {
        this.transferAssertionSteps = new TransferAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
        this.dbAssertionSteps = new DbAssertionSteps(softly);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @FraudMockStatus(FraudStatus.APPROVED)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void shouldExecuteTransferWhenFraudApproved(double amount) {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts().getAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.decreasedBy(firstUserAccount, transferRequestBody.getAmount());
        var expectedReceiverDbAccount = ExpectedAccountState.increasedBy(firstUserSecondAccount, transferRequestBody.getAmount());

        var transferResponse = TransferSteps.sendTransferWithFraudRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse, FraudStatus.APPROVED.getStatusMessage());

        var accountsAfterRequest = userSteps.getUserAccounts().getAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount, transferRequestBody.getAmount());
        this.accountAssertionSteps.assertBalanceWasDecreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount, transferRequestBody.getAmount());

        var senderTransactions = userSteps.getAccountTransactions(firstUserAccount.getId());
        var receiverTransactions = userSteps.getAccountTransactions(firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(
                senderTransactions,
                transferRequestBody.getAmount(),
                firstUserSecondAccount.getId()
        );
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(    receiverTransactions, transferRequestBody.getAmount(), firstUserAccount.getId());

        var senderTrxId = senderTransactions.getTransactions()
                .stream()
                .filter(tr -> tr.getType().equals(TransactionType.TRANSFER_OUT))
                .findFirst()
                .get()
                .getId();
        var receiverTrxId = receiverTransactions.getTransactions()
                .stream()
                .filter(tr -> tr.getType().equals(TransactionType.TRANSFER_IN))
                .findFirst()
                .get()
                .getId();
        TransferDbAssertions.assertTransferTransactionsPersisted(this.dbAssertionSteps, transferResponse, senderTrxId, receiverTrxId);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, false);

        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(receiverDbAccountAfterRequest, expectedReceiverDbAccount, false);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @FraudMockStatus(FraudStatus.MANUAL_REVIEW_REQUIRED)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void shouldNotExecuteTransferWhenManualReviewIsRequired(double amount) {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var transferResponse = TransferSteps.sendTransferWithFraudRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());
        assertTransferNotProcessed(
                transferRequestBody,
                transferResponse,
                accountsBeforeRequest,
                expectedSenderDbAccount,
                expectedReceiverDbAccount,
                FraudStatus.MANUAL_REVIEW_REQUIRED
        );
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @FraudMockStatus(FraudStatus.VERIFICATION_REQUIRED)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void shouldNotExecuteTransferWhenVerificationIsRequired(double amount) {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var transferResponse = TransferSteps.sendTransferWithFraudRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());

        assertTransferNotProcessed(
                transferRequestBody,
                transferResponse,
                accountsBeforeRequest,
                expectedSenderDbAccount,
                expectedReceiverDbAccount,
                FraudStatus.VERIFICATION_REQUIRED
        );
    }

    private void assertTransferNotProcessed(
            TransferRequestBody transferRequestBody,
            TransferResponseBody transferResponse,
            CustomerAccountsResponseBody accountsBeforeRequest,
            AccountDao expectedSenderDbAccount,
            AccountDao expectedReceiverDbAccount,
            FraudStatus fraudStatus
    ) {
        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse, fraudStatus.getStatusMessage());

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());

        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, true);
        this.dbAssertionSteps.assertAccountDaoEquals(expectedReceiverDbAccount, receiverDbAccountAfterRequest, true);

    }
}

