package api.transactions;

import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.utils.TransactionsListUtils;
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

@Execution(ExecutionMode.SAME_THREAD)
public class TransferMoneyWithFraudApiTest extends BaseMockTest {
    private AccountResponseBody firstUserAccount;
    private AccountResponseBody firstUserSecondAccount;
    private UserModel firstUser;
    private TransferAssertionSteps transferAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;
    private DbAssertionSteps dbAssertionSteps;

    @Step("Prepare funded sender account")
    public void prepareFundedSenderAccount() {
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
    @DisplayName("Transfer is processed when fraud check is approved")
    public void shouldProcessTransferWhenFraudCheckIsApproved(double amount) {
        prepareFundedSenderAccount();
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
        this.accountAssertionSteps.assertAccountHasLatestTransaction(
                senderTransactions,
                TransactionType.TRANSFER_OUT,
                transferRequestBody.getAmount(),
                firstUserSecondAccount.getId()
        );
        this.accountAssertionSteps.assertAccountHasLatestTransaction(
                receiverTransactions,
                TransactionType.TRANSFER_IN,
                transferRequestBody.getAmount(),
                firstUserAccount.getId()
        );

        var senderTrxId = TransactionsListUtils.findFirstTransactionWithType(
                        senderTransactions.getTransactions(),
                        TransactionType.TRANSFER_OUT
                )
                .getId();
        var receiverTrxId = TransactionsListUtils.findFirstTransactionWithType(
                        receiverTransactions.getTransactions(),
                        TransactionType.TRANSFER_IN
                )
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
    @DisplayName("Transfer is not processed when manual review is required")
    public void shouldNotProcessTransferWhenManualReviewIsRequired(double amount) {
        prepareFundedSenderAccount();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var transferResponse = TransferSteps.sendTransferWithFraudRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());
        assertTransferWasNotProcessed(
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
    @DisplayName("Transfer is not processed when additional verification is required")
    public void shouldNotProcessTransferWhenVerificationIsRequired(double amount) {
        prepareFundedSenderAccount();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var transferResponse = TransferSteps.sendTransferWithFraudRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());

        assertTransferWasNotProcessed(
                transferRequestBody,
                transferResponse,
                accountsBeforeRequest,
                expectedSenderDbAccount,
                expectedReceiverDbAccount,
                FraudStatus.VERIFICATION_REQUIRED
        );
    }


    @Test
    @UserSession(accountsNumber = 1)
    @FraudMockStatus(FraudStatus.APPROVED)
    @DisplayName("Transfer with fraud check request is rejected when auth header is empty")
    public void shouldRejectUnauthorisedTransferWithFraudRequest() {
        prepareFundedSenderAccount();
        var transferRequestBody = TransferRequestGenerator.generateWithReceiver(firstUserAccount.getId());

        var response = TransferSteps.sendTransferWithFraudRequestWithStringResponse(
                RequestSpecs.unauthSpec(),
                transferRequestBody,
                ResponseSpecs.unauthorized()
        );

        this.stringAssertionsSteps.assertTextIsEmpty(response);
    }

    private void assertTransferWasNotProcessed(
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

