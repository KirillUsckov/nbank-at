package api.transactions;

import common.BaseMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.api.models.body.request.TransferRequestBody;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.models.body.response.customer.profile.CustomerAccountsResponseBody;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.mock.annotations.FraudMockStatus;
import ru.kduskov.mock.data.MockDataProvider;
import ru.kduskov.mock.enums.FraudStatus;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.generators.TransferRequestGenerator;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.api.steps.DepositSteps;
import ru.kduskov.api.steps.TransferSteps;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.api.steps.assertions.TransferAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.assertions.OptionalAssert;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.db.steps.DbAssertionSteps;
import ru.kduskov.db.steps.SqlSteps;
import ru.kduskov.ui.models.UserModel;

import java.math.BigDecimal;

import static common.Constans.*;

public class TransferMoneyWithFraudApiTest extends BaseMockTest {
    private AccountResponseBody firstUserAccount;
    private AccountResponseBody firstUserSecondAccount;
    private UserModel firstUser;
    private TransferAssertionSteps transferAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;
    private DbAssertionSteps dbAssertionSteps;

    public void setUpTestData() {
        firstUser = SessionStorage.getUser(FIRST_USER_ID);
        firstUserAccount = SessionStorage.getUserAccount(firstUser.getUsername(), FIRST_ACC_ID);
        DepositSteps.sendDepositWithAmountValidation(firstUserAccount, firstUser.getToken(), 50_000);
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

        var expectedSenderDbAccount = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        expectedSenderDbAccount.setBalance(expectedSenderDbAccount.getBalance().subtract(BigDecimal.valueOf(transferRequestBody.getAmount())));

        var expectedReceiverDbAccount = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());
        expectedReceiverDbAccount.setBalance(expectedReceiverDbAccount.getBalance().add(BigDecimal.valueOf(transferRequestBody.getAmount())));

        var transferResponse = TransferSteps.sendTransferWithFraudRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse, MockDataProvider.fraudResponse(FraudStatus.APPROVED), FraudStatus.APPROVED.getStatusMessage());

        var accountsAfterRequest = userSteps.getUserAccounts().getAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount, transferRequestBody.getAmount());
        this.accountAssertionSteps.assertBalanceWasDecreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount, transferRequestBody.getAmount());

        var senderTransactions = userSteps.getAccountTransactions(firstUserAccount.getId());
        var receiverTransactions = userSteps.getAccountTransactions(firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderTransactions, transferRequestBody.getAmount(), firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverTransactions, transferRequestBody.getAmount(), firstUserAccount.getId());

        var senderTrxId = senderTransactions.getTransactions().stream().filter(tr -> tr.getType().equals(TransactionType.TRANSFER_OUT)).findFirst().get().getId();
        var receiverTrxId = receiverTransactions.getTransactions().stream().filter(tr -> tr.getType().equals(TransactionType.TRANSFER_IN)).findFirst().get().getId();
        var senderTransactionOpt = SqlSteps.findTransactionById(senderTrxId);
        var receiverTransactionOpt = SqlSteps.findTransactionById(receiverTrxId);
        OptionalAssert.assertThat(senderTransactionOpt).isPresent();
        OptionalAssert.assertThat(receiverTransactionOpt).isPresent();

        this.dbAssertionSteps.assertTransactionDaoEquals(senderTransactionOpt.get(), senderTrxId, transferResponse.getSenderAccountId(), transferResponse.getReceiverAccountId(), transferResponse.getAmount(), TransactionType.TRANSFER_OUT);
        this.dbAssertionSteps.assertTransactionDaoEquals(receiverTransactionOpt.get(), receiverTrxId, transferResponse.getReceiverAccountId(), transferResponse.getSenderAccountId(), transferResponse.getAmount(), TransactionType.TRANSFER_IN);

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

        var expectedSenderDbAccount = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        var expectedReceiverDbAccount = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var transferResponse = TransferSteps.sendTransferWithFraudRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());
        assertTransferNotProcessed(transferRequestBody, transferResponse, accountsBeforeRequest, expectedSenderDbAccount, expectedReceiverDbAccount, FraudStatus.MANUAL_REVIEW_REQUIRED);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @FraudMockStatus(FraudStatus.VERIFICATION_REQUIRED)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void shouldNotExecuteTransferWhenVerificationIsRequired(double amount) {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        var expectedReceiverDbAccount = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var transferResponse = TransferSteps.sendTransferWithFraudRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());

        assertTransferNotProcessed(transferRequestBody, transferResponse, accountsBeforeRequest, expectedSenderDbAccount, expectedReceiverDbAccount, FraudStatus.VERIFICATION_REQUIRED);
    }

    private void assertTransferNotProcessed(TransferRequestBody transferRequestBody, TransferResponseBody transferResponse, CustomerAccountsResponseBody accountsBeforeRequest, AccountDao expectedSenderDbAccount, AccountDao expectedReceiverDbAccount, FraudStatus fraudStatus) {
        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse, MockDataProvider.fraudResponse(fraudStatus), fraudStatus.getStatusMessage());

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
