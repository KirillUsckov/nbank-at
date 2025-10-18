package tests;

import constants.ErrorMessages;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.generators.TransferRequestGenerator;
import ru.kduskov.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import ru.kduskov.steps.DepositSteps;
import ru.kduskov.steps.TransferSteps;
import ru.kduskov.steps.UserSteps;
import ru.kduskov.utils.AccountsListUtils;
import steps.assertions.AccountAssertionSteps;
import steps.assertions.TransferAssertionSteps;

public class TransferMoneyTest extends BaseTest {
    private static AccountResponseBody firstUserFirstAccount;
    private static AccountResponseBody firstUserSecondAccount;
    private static AccountResponseBody secondUserAccount;
    private static String secondUserAuthToken;
    private final TransferSteps transferSteps = new TransferSteps();
    private TransferAssertionSteps transferAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;

    @BeforeAll
    public static void createAccounts() {
        firstUserFirstAccount = UserSteps.createAccount(userAuthToken);
        DepositSteps.sendDepositWithAmountValidation(firstUserFirstAccount, userAuthToken, 50_000);
        firstUserSecondAccount = UserSteps.createAccount(userAuthToken);

        secondUserAuthToken = UserSteps.createRandomUser();
        secondUserAccount = UserSteps.createAccount(secondUserAuthToken);
    }

    @BeforeEach
    public void initAssertionClasses() {
        this.transferAssertionSteps = new TransferAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCanMakeTransferToHisAnotherAccountWithValidAmountIfMoneyEnough(double amount) {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);
        var transferRequestBody = TransferRequestGenerator.generateWithSender(firstUserFirstAccount.getId(), firstUserSecondAccount.getId(), amount);

        var transferResponse = new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok(), Endpoint.TRANSFER)
                .post(transferRequestBody);

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount, amount);
        this.accountAssertionSteps.assertBalanceWasDecreased(
                accountsBeforeRequest, accountsAfterRequest, firstUserFirstAccount, amount);

        var senderAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest, firstUserFirstAccount);
        var receiverAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest, firstUserSecondAccount);
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderAccountAfter, amount, firstUserSecondAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverAccountAfter, amount, firstUserFirstAccount.getId());
    }


    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCanMakeTransferToAnotherUserAccountWithValidAmountIfMoneyEnough(double amount) {
        var senderAccountsBefore = userSteps.getUserAccounts(userAuthToken);
        var receiverAccountsBefore = userSteps.getUserAccounts(secondUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generateWithSender(firstUserFirstAccount.getId(), secondUserAccount.getId(), amount);

        var transferResponse = new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok(), Endpoint.TRANSFER)
                .post(transferRequestBody);

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse);

        var senderAccountsAfter = userSteps.getUserAccounts(userAuthToken);
        var receiverAccountsAfter = userSteps.getUserAccounts(secondUserAuthToken);

        this.accountAssertionSteps.assertBalanceWasIncreased(
                senderAccountsBefore, senderAccountsAfter, firstUserFirstAccount, -amount);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount, amount);

        var senderAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                senderAccountsAfter, firstUserFirstAccount);
        var receiverAccountAfter = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                receiverAccountsAfter, secondUserAccount);
        this.accountAssertionSteps.assertAccountHasLatestTransferOut(senderAccountAfter, amount, secondUserAccount.getId());
        this.accountAssertionSteps.assertAccountHasLatestTransferIn(receiverAccountAfter, amount, firstUserFirstAccount.getId());

    }


    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCantMakeTransferToAnotherUserAccountWithValidAmountIfMoneyNotEnough(double amount) {
        var accountWithNoMoney = UserSteps.createAccount(userAuthToken);
        var senderAccountsBefore = userSteps.getUserAccounts(userAuthToken);
        var receiverAccountsBefore = userSteps.getUserAccounts(secondUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generateWithSender(accountWithNoMoney.getId(), secondUserAccount.getId(), amount);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, userAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var senderAccountsAfter = userSteps.getUserAccounts(userAuthToken);
        var receiverAccountsAfter = userSteps.getUserAccounts(secondUserAuthToken);

        this.accountAssertionSteps.assertBalanceWasNotChanged(
                senderAccountsBefore, senderAccountsAfter, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCantMakeTransferToOwnUserAccountWithValidAmountIfMoneyNotEnough(double amount) {
        var accountWithNoMoney = UserSteps.createAccount(userAuthToken);
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);

        var transferRequestBody = TransferRequestGenerator.generateWithSender(accountWithNoMoney.getId(), firstUserSecondAccount.getId(), amount);
        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, userAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 0})
    public void checkUserCantMakeTransferWithTooLowAmount(double amount) {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);
        var transferRequestBody = TransferRequestGenerator.generateWithSender(firstUserFirstAccount.getId(), firstUserSecondAccount.getId(), amount);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, userAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.TRANSFER_AMOUNT_MUST_BE_AT_LEAST_MIN, message);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserFirstAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    public void checkUserCantMakeTransferWithTooBigAmount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);

        var transferRequestBody = TransferRequestGenerator.generateWithSender(firstUserFirstAccount.getId(), firstUserSecondAccount.getId(), 10_000.01);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, userAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.TRANSFER_AMOUNT_CANNOT_EXCEED_MAX, message);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserFirstAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    public void checkUserCantMakeTransferToNotExistedAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);

        var transferRequestBody = TransferRequestGenerator.generateWithSender(firstUserFirstAccount.getId(), 100000000L);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, userAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserFirstAccount);
    }

    @Test
    public void checkUserCantMakeTransferFromNotExistedAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);

        var transferRequestBody = TransferRequestGenerator.generateWithReceiver(firstUserSecondAccount.getId());

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, userAuthToken, ResponseSpecs.accessForbidden());
        this.accountAssertionSteps.assertMessage(ErrorMessages.Account.UNAUTHORIZED_ACCESS_TO_ACCOUNT, message);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }
}
