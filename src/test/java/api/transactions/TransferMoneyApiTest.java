package api.transactions;


import support.ExpectedAccountState;
import support.TransactionTestData;
import support.TransferDbAssertions;
import common.BaseTest;
import ru.kduskov.api.constants.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.generators.TransferRequestGenerator;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.api.steps.AccountSteps;
import ru.kduskov.api.steps.TransferSteps;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.api.steps.assertions.TransferAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.db.steps.DbAssertionSteps;
import ru.kduskov.db.steps.SqlSteps;
import ru.kduskov.ui.models.UserModel;


import static common.Constans.*;

public class TransferMoneyApiTest extends BaseTest {
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
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCanMakeTransferToHisAnotherAccountWithValidAmountIfMoneyEnough(double amount) {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts().getAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.decreasedBy(firstUserAccount, amount);
        var expectedReceiverDbAccount = ExpectedAccountState.increasedBy(firstUserSecondAccount, amount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);

        var transferResponse = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse, "Transfer successful");

        var accountsAfterRequest = userSteps.getUserAccounts().getAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount, amount);
        this.accountAssertionSteps.assertBalanceWasDecreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount, amount);

        var senderTransactions = userSteps.getAccountTransactions(firstUserAccount.getId());
        var receiverTransactions = userSteps.getAccountTransactions(firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderTransactions, amount, firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverTransactions, amount, firstUserAccount.getId());

        var senderTrxId = senderTransactions.getTransactions().stream().filter(tr->tr.getType().equals(TransactionType.TRANSFER_OUT)).findFirst().get().getId();
        var receiverTrxId = receiverTransactions.getTransactions().stream().filter(tr->tr.getType().equals(TransactionType.TRANSFER_IN)).findFirst().get().getId();
        TransferDbAssertions.assertTransferTransactionsPersisted(this.dbAssertionSteps, transferResponse, senderTrxId, receiverTrxId);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, false);

        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(receiverDbAccountAfterRequest, expectedReceiverDbAccount, false);
    }

    @ParameterizedTest
    @UserSession(usersNumber = 2, accountsNumber = 2)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCanMakeTransferToAnotherUserAccountWithValidAmountIfMoneyEnough(double amount) {
        setUpTestData();
        var senderAccountsBefore = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts().getAccounts();
        var receiverAccountsBefore = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts().getAccounts();
        var secondUserAccount = SessionStorage.getUserAccounts(SECOND_USER_ID).stream().findFirst().orElseThrow();

        var expectedSenderDbAccount = ExpectedAccountState.decreasedBy(firstUserAccount, amount);
        var expectedReceiverDbAccount = ExpectedAccountState.increasedBy(secondUserAccount, amount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), secondUserAccount.getId(), amount);

        var transferResponse = new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.ok(), Endpoint.TRANSFER)
                .post(transferRequestBody);

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse, "Transfer successful");

        var senderAccountsAfter = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts().getAccounts();
        var receiverAccountsAfter = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts().getAccounts();

        this.accountAssertionSteps.assertBalanceWasDecreased(
                senderAccountsBefore, senderAccountsAfter, firstUserAccount, amount);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount, amount);

        var senderTransactions = SessionStorage.getUserSteps(FIRST_USER_ID).getAccountTransactions(firstUserAccount.getId());
        var receiverTransactions = SessionStorage.getUserSteps(SECOND_USER_ID).getAccountTransactions(secondUserAccount.getId());

        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderTransactions, amount, secondUserAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverTransactions, amount, firstUserAccount.getId());

        var senderTrxId = senderTransactions.getTransactions().stream().filter(tr->tr.getType().equals(TransactionType.TRANSFER_OUT)).findFirst().get().getId();
        var receiverTrxId = receiverTransactions.getTransactions().stream().filter(tr->tr.getType().equals(TransactionType.TRANSFER_IN)).findFirst().get().getId();
        TransferDbAssertions.assertTransferTransactionsPersisted(this.dbAssertionSteps, transferResponse, senderTrxId, receiverTrxId);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, false);

        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(secondUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(receiverDbAccountAfterRequest, expectedReceiverDbAccount, false);
    }

    @ParameterizedTest
    @UserSession(usersNumber = 2, accountsNumber = 1)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCantMakeTransferToAnotherUserAccountWithValidAmountIfMoneyNotEnough(double amount) {
        setUpTestData();
        var accountWithNoMoney = AccountSteps.createAccount(firstUser.getToken());
        var senderAccountsBefore = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var receiverAccountsBefore = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts();
        var secondUserAccount = SessionStorage.getUserAccounts(SECOND_USER_ID).stream().findFirst().orElseThrow();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(accountWithNoMoney);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(secondUserAccount);

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney.getId(), secondUserAccount.getId(), amount);

        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, response.getMessage());

        var senderAccountsAfter = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var receiverAccountsAfter = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts();

        this.accountAssertionSteps.assertBalanceWasNotChanged(
                senderAccountsBefore, senderAccountsAfter, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount);

        var accountWithNoMoneyTransactions = SessionStorage.getUserSteps(FIRST_USER_ID).getAccountTransactions(accountWithNoMoney.getId());
        this.accountAssertionSteps.assertAccountHasNoTransactionsWithType(accountWithNoMoneyTransactions, TransactionType.TRANSFER_OUT);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(accountWithNoMoney.getAccountNumber());
        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(secondUserAccount.getAccountNumber());

        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, true);
        this.dbAssertionSteps.assertAccountDaoEquals(expectedReceiverDbAccount, receiverDbAccountAfterRequest, true);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCantMakeTransferToOwnUserAccountWithValidAmountIfMoneyNotEnough(double amount) {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountWithNoMoney = AccountSteps.createAccount(firstUser.getToken());
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(accountWithNoMoney);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney.getId(), firstUserSecondAccount.getId(), amount);
        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, response.getMessage());

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);

        var accountWithNoMoneyTransactions = SessionStorage.getUserSteps(FIRST_USER_ID).getAccountTransactions(accountWithNoMoney.getId());
        this.accountAssertionSteps.assertAccountHasNoTransactionsWithType(accountWithNoMoneyTransactions, TransactionType.TRANSFER_OUT);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(accountWithNoMoney.getAccountNumber());
        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());

        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, true);
        this.dbAssertionSteps.assertAccountDaoEquals(expectedReceiverDbAccount, receiverDbAccountAfterRequest, true);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @ValueSource(doubles = {-0.01, 0})
    public void checkUserCantMakeTransferWithTooLowAmount(double amount) {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, response.getMessage());

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

    @Test
    @UserSession(accountsNumber = 2)
    public void checkUserCantMakeTransferWithTooBigAmount() {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), 10_000.01);

        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.TRANSFER_AMOUNT_CANNOT_EXCEED_MAX, response.getMessage());

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

    @Test
    @UserSession(accountsNumber = 2)
    public void checkUserCantMakeTransferToNotExistedAccount() {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), 100000000L);

        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, response.getMessage());

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, true);
    }

    @Test
    @UserSession(accountsNumber = 2)
    public void checkUserCantMakeTransferFromNotExistedAccount() {
        setUpTestData();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generateWithReceiver(firstUserSecondAccount.getId());

        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.accessForbidden());
        this.accountAssertionSteps.assertMessage(ErrorMessages.Account.UNAUTHORIZED_ACCESS_TO_ACCOUNT, response.getMessage());

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);

        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(receiverDbAccountAfterRequest, expectedReceiverDbAccount, true);
    }
}

