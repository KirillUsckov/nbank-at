package api.transactions;

import api.BaseTest;
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
import ru.kduskov.api.steps.DepositSteps;
import ru.kduskov.api.steps.TransferSteps;
import ru.kduskov.api.utils.AccountsListUtils;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.api.steps.assertions.TransferAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.ui.models.UserModel;

import static common.Constans.*;

public class TransferMoneyTest extends BaseTest {
    private AccountResponseBody firstUserAccount;
    private AccountResponseBody firstUserSecondAccount;
    private UserModel firstUser;
    private final TransferSteps transferSteps = new TransferSteps();
    private TransferAssertionSteps transferAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;

    public void setUpTestData() {
        firstUser = SessionStorage.getUser(FIRST_USER_ID);
        firstUserAccount = SessionStorage.getUserAccount(firstUser.getUsername(), FIRST_ACC_ID);
        firstUserSecondAccount = SessionStorage.getUserAccount(firstUser.getUsername(), SECOND_ACC_ID);
        DepositSteps.sendDepositWithAmountValidation(firstUserAccount, firstUser.getToken(), 50_000);
    }

    @BeforeEach
    public void initAssertionClasses() {
        this.transferAssertionSteps = new TransferAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCanMakeTransferToHisAnotherAccountWithValidAmountIfMoneyEnough(double amount) {
        setUpTestData();
        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts();
        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);

        var transferResponse = new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.ok(), Endpoint.TRANSFER)
                .post(transferRequestBody);

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse);

        var accountsAfterRequest = userSteps.getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount, amount);
        this.accountAssertionSteps.assertBalanceWasDecreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount, amount);

        var senderAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest, firstUserAccount);
        var receiverAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest, firstUserSecondAccount);
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderAccountAfter, amount, firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverAccountAfter, amount, firstUserAccount.getId());
    }

    @ParameterizedTest
    @UserSession(usersNumber = 2, accountsNumber = 2)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCanMakeTransferToAnotherUserAccountWithValidAmountIfMoneyEnough(double amount) {
        setUpTestData();
        var senderAccountsBefore = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var receiverAccountsBefore = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts();
        var secondUserAccount = SessionStorage.getUserAccounts(SECOND_USER_ID).stream().findFirst().orElseThrow();

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), secondUserAccount.getId(), amount);

        var transferResponse = new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.ok(), Endpoint.TRANSFER)
                .post(transferRequestBody);

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse);

        var senderAccountsAfter = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var receiverAccountsAfter = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts();

        this.accountAssertionSteps.assertBalanceWasDecreased(
                senderAccountsBefore, senderAccountsAfter, firstUserAccount, amount);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount, amount);

        var senderAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                senderAccountsAfter, firstUserAccount);
        var receiverAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                receiverAccountsAfter, secondUserAccount);
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderAccountAfter, amount, secondUserAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverAccountAfter, amount, firstUserAccount.getId());
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

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney.getId(), secondUserAccount.getId(), amount);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var senderAccountsAfter = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var receiverAccountsAfter = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts();

        this.accountAssertionSteps.assertBalanceWasNotChanged(
                senderAccountsBefore, senderAccountsAfter, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount);

        this.accountAssertionSteps.assertAccountHasNoTransactionsWithType(accountWithNoMoney, TransactionType.TRANSFER_OUT);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCantMakeTransferToOwnUserAccountWithValidAmountIfMoneyNotEnough(double amount) {
        setUpTestData();
        var accountWithNoMoney = AccountSteps.createAccount(firstUser.getToken());
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney.getId(), firstUserSecondAccount.getId(), amount);
        var message = this.transferSteps.sendTransferRequestWithStringResponse(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);

        this.accountAssertionSteps.assertAccountHasNoTransactionsWithType(accountWithNoMoney, TransactionType.TRANSFER_OUT);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 2)
    @ValueSource(doubles = {-0.01, 0})
    public void checkUserCantMakeTransferWithTooLowAmount(double amount) {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.TRANSFER_AMOUNT_MUST_BE_AT_LEAST_MIN, message);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    @UserSession(accountsNumber = 2)
    public void checkUserCantMakeTransferWithTooBigAmount() {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), 10_000.01);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.TRANSFER_AMOUNT_CANNOT_EXCEED_MAX, message);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    @UserSession(accountsNumber = 2)
    public void checkUserCantMakeTransferToNotExistedAccount() {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), 100000000L);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(firstUser.getToken(), transferRequestBody, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }

    @Test
    @UserSession(accountsNumber = 2)
    public void checkUserCantMakeTransferFromNotExistedAccount() {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();

        var transferRequestBody = TransferRequestGenerator.generateWithReceiver(firstUserSecondAccount.getId());

        var message = this.transferSteps.sendTransferRequestWithStringResponse(firstUser.getToken(), transferRequestBody, ResponseSpecs.accessForbidden());
        this.accountAssertionSteps.assertMessage(ErrorMessages.Account.UNAUTHORIZED_ACCESS_TO_ACCOUNT, message);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }
}
