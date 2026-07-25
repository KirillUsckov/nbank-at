package api.transactions;

import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import ru.kduskov.api.utils.TransactionsListUtils;
import support.ExpectedAccountState;
import support.TransactionTestData;
import support.TransferDbAssertions;
import common.BaseTest;
import ru.kduskov.api.constants.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.api.generators.TransferRequestGenerator;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
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

import static common.Constans.FIRST_USER_ID;
import static common.Constans.FIRST_ACC_ID;
import static common.Constans.SECOND_ACC_ID;
import static common.Constans.SECOND_USER_ID;
import static ru.kduskov.api.enums.TransactionType.TRANSFER_IN;
import static ru.kduskov.api.enums.TransactionType.TRANSFER_OUT;

public class TransferMoneyApiTest extends BaseTest {
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
    public void initializeAssertionSteps() {
        this.transferAssertionSteps = new TransferAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
        this.dbAssertionSteps = new DbAssertionSteps(softly);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    @DisplayName("User transfers money to another own account when balance is sufficient")
    public void shouldTransferMoneyToAnotherOwnAccountWhenBalanceIsSufficient(double amount) {
        prepareFundedSenderAccount();
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

        var senderTrans = userSteps.getAccountTransactions(firstUserAccount.getId());
        var receiverTrans = userSteps.getAccountTransactions(firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransaction(senderTrans, TRANSFER_OUT, amount, firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransaction(receiverTrans, TRANSFER_IN, amount, firstUserAccount.getId());

        var senderTrxId = TransactionsListUtils.findFirstTransactionWithType(senderTrans.getTransactions(), TRANSFER_OUT).getId();
        var receiverTrxId = TransactionsListUtils.findFirstTransactionWithType(receiverTrans.getTransactions(), TRANSFER_IN).getId();

        TransferDbAssertions.assertTransferTransactionsPersisted(this.dbAssertionSteps, transferResponse, senderTrxId, receiverTrxId);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, false);

        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(receiverDbAccountAfterRequest, expectedReceiverDbAccount, false);
    }

    @ParameterizedTest
    @UserSession(usersNumber = 2, accountsNumber = 2)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    @DisplayName("Transfer to another user's account succeeds when balance is sufficient")
    public void shouldTransferMoneyToAnotherUserWhenBalanceIsSufficient(double amount) {
        prepareFundedSenderAccount();
        var senderAccountsBefore = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts().getAccounts();
        var receiverAccountsBefore = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts().getAccounts();
        var secondUserAccount = SessionStorage.getUserAccounts(SECOND_USER_ID).stream().findFirst().orElseThrow();

        var expectedSenderDbAccount = ExpectedAccountState.decreasedBy(firstUserAccount, amount);
        var expectedReceiverDbAccount = ExpectedAccountState.increasedBy(secondUserAccount, amount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), secondUserAccount.getId(), amount);

        var transferResponse = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.ok());

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse, "Transfer successful");

        var senderAccountsAfter = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts().getAccounts();
        var receiverAccountsAfter = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts().getAccounts();

        this.accountAssertionSteps.assertBalanceWasDecreased(
                senderAccountsBefore, senderAccountsAfter, firstUserAccount, amount);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount, amount);

        var senderTransactions = SessionStorage.getUserSteps(FIRST_USER_ID).getAccountTransactions(firstUserAccount.getId());
        var receiverTransactions = SessionStorage.getUserSteps(SECOND_USER_ID).getAccountTransactions(secondUserAccount.getId());

        this.accountAssertionSteps.assertAccountHasLatestTransaction(
                senderTransactions,
                TRANSFER_OUT,
                amount,
                secondUserAccount.getId()
        );
        this.accountAssertionSteps.assertAccountHasLatestTransaction(
                receiverTransactions,
                TRANSFER_IN,
                amount,
                firstUserAccount.getId()
        );

        var senderTrxId = TransactionsListUtils.findFirstTransactionWithType(senderTransactions.getTransactions(), TRANSFER_OUT).getId();
        var receiverTrxId = TransactionsListUtils.findFirstTransactionWithType(receiverTransactions.getTransactions(), TRANSFER_IN).getId();

        TransferDbAssertions.assertTransferTransactionsPersisted(this.dbAssertionSteps, transferResponse, senderTrxId, receiverTrxId);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, false);

        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(secondUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(receiverDbAccountAfterRequest, expectedReceiverDbAccount, false);
    }

    @ParameterizedTest
    @UserSession(usersNumber = 2, accountsNumber = 1)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    @DisplayName("Transfer to another user is rejected when balance is insufficient")
    public void shouldRejectTransferToAnotherUserWhenBalanceIsInsufficient(double amount) {
        prepareFundedSenderAccount();
        var accountWithNoMoney = AccountSteps.createAccount(firstUser.getToken());
        var senderAccountsBefore = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var receiverAccountsBefore = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts();
        var secondUserAccount = SessionStorage.getUserAccounts(SECOND_USER_ID).stream().findFirst().orElseThrow();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(accountWithNoMoney);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(secondUserAccount);

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney.getId(), secondUserAccount.getId(), amount);

        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.stringAssertionsSteps.assertTextEqualsTo(
                ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS,
                response.getMessage()
        );

        var senderAccountsAfter = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var receiverAccountsAfter = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts();

        this.accountAssertionSteps.assertBalanceWasNotChanged(
                senderAccountsBefore, senderAccountsAfter, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount);

        var accountWithNoMoneyTransactions = SessionStorage.getUserSteps(FIRST_USER_ID).getAccountTransactions(accountWithNoMoney.getId());
        this.accountAssertionSteps.assertAccountHasNoTransactionsWithType(accountWithNoMoneyTransactions, TRANSFER_OUT);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(accountWithNoMoney.getAccountNumber());
        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(secondUserAccount.getAccountNumber());

        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, true);
        this.dbAssertionSteps.assertAccountDaoEquals(expectedReceiverDbAccount, receiverDbAccountAfterRequest, true);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    @DisplayName("Transfer between own accounts is rejected when balance is insufficient")
    public void shouldRejectTransferBetweenOwnAccountsWhenBalanceIsInsufficient(double amount) {
        prepareFundedSenderAccount();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountWithNoMoney = AccountSteps.createAccount(firstUser.getToken());
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(accountWithNoMoney);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney.getId(), firstUserSecondAccount.getId(), amount);
        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.stringAssertionsSteps.assertTextEqualsTo(
                ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS,
                response.getMessage()
        );

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);

        var accountWithNoMoneyTransactions = SessionStorage.getUserSteps(FIRST_USER_ID).getAccountTransactions(accountWithNoMoney.getId());
        this.accountAssertionSteps.assertAccountHasNoTransactionsWithType(accountWithNoMoneyTransactions, TRANSFER_OUT);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(accountWithNoMoney.getAccountNumber());
        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());

        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, true);
        this.dbAssertionSteps.assertAccountDaoEquals(expectedReceiverDbAccount, receiverDbAccountAfterRequest, true);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @ValueSource(doubles = {-0.01, 0})
    @DisplayName("Transfer is rejected when amount is zero or negative")
    public void shouldRejectTransferWhenAmountIsNotPositive(double amount) {
        prepareFundedSenderAccount();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);
        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.stringAssertionsSteps.assertTextEqualsTo(
                ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS,
                response.getMessage()
        );

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
    @DisplayName("Transfer is rejected when amount exceeds the maximum limit")
    public void shouldRejectTransferWhenAmountExceedsMaximumLimit() {
        prepareFundedSenderAccount();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), 10_000.01);

        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.stringAssertionsSteps.assertTextEqualsTo(ErrorMessages.Transfer.TRANSFER_AMOUNT_CANNOT_EXCEED_MAX, response.getMessage());

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
    @DisplayName("Transfer is rejected when receiver account does not exist")
    public void shouldRejectTransferWhenReceiverAccountDoesNotExist() {
        prepareFundedSenderAccount();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var expectedSenderDbAccount = ExpectedAccountState.unchanged(firstUserAccount);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), 100000000L);

        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.stringAssertionsSteps.assertTextEqualsTo(
                ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS,
                response.getMessage()
        );

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);

        var senderDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(senderDbAccountAfterRequest, expectedSenderDbAccount, true);
    }

    @Test
    @UserSession(accountsNumber = 2)
    @DisplayName("Transfer is rejected when sender account does not exist")
    public void shouldRejectTransferWhenSenderAccountDoesNotExist() {
        prepareFundedSenderAccount();
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var expectedReceiverDbAccount = ExpectedAccountState.unchanged(firstUserSecondAccount);

        var transferRequestBody = TransferRequestGenerator.generateWithReceiver(firstUserSecondAccount.getId());

        var response = TransferSteps.sendTransferRequest(firstUser.getToken(), transferRequestBody, ResponseSpecs.accessForbidden());
        this.stringAssertionsSteps.assertTextEqualsTo(ErrorMessages.Account.UNAUTHORIZED_ACCESS_TO_ACCOUNT, response.getMessage());

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);

        var receiverDbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserSecondAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(receiverDbAccountAfterRequest, expectedReceiverDbAccount, true);
    }

    @Test
    @UserSession(accountsNumber = 1)
    @DisplayName("Transfer request is rejected when auth header is empty")
    public void shouldRejectUnauthorisedDepositRequest() {
        prepareFundedSenderAccount();
        var transferRequestBody = TransferRequestGenerator.generateWithReceiver(firstUserAccount.getId());

        var response = TransferSteps.sendTransferRequestWithStringResponse(
                RequestSpecs.unauthSpec(),
                transferRequestBody,
                ResponseSpecs.unauthorized()
        );

        this.stringAssertionsSteps.assertTextIsEmpty(response);
    }
}

