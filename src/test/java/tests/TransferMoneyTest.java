package tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.generators.RandomData;
import ru.kduskov.generators.RequestDataGenerator;
import ru.kduskov.generators.TransferRequestGenerator;
import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import ru.kduskov.steps.DepositSteps;
import ru.kduskov.steps.UserSteps;
import ru.kduskov.utils.AccountsListUtils;
import steps.assertions.AccountAssertionSteps;
import steps.assertions.TransferAssertionSteps;

public class TransferMoneyTest extends BaseTest {
    private static AccountResponseBody firstUserFirstAccount;
    private static AccountResponseBody firstUserSecondAccount;
    private static AccountResponseBody secondUserAccount;
    private static String secondUserAuthToken;
    private TransferAssertionSteps transferAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;

    @BeforeAll
    public static void createAccounts() {
        firstUserFirstAccount = UserSteps.createAccount(userAuthToken);
        DepositSteps.makeDepositWithAmountValidation(firstUserFirstAccount, userAuthToken, 50_000);
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
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCanMakeTransferToHisAnotherAccountWithValidAmountIfMoneyEnough(int amount) {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);
        var transferRequestBody = TransferRequestGenerator.generate(firstUserFirstAccount, firstUserSecondAccount, amount);

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
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCanMakeTransferToAnotherUserAccountWithValidAmountIfMoneyEnough(int amount) {
        var senderAccountsBefore = userSteps.getUserAccounts(userAuthToken);
        var receiverAccountsBefore = userSteps.getUserAccounts(secondUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserFirstAccount, secondUserAccount, amount);

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
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCantMakeTransferToAnotherUserAccountWithValidAmountIfMoneyNotEnough(int amount) {
        var accountWithNoMoney = UserSteps.createAccount(userAuthToken);
        var senderAccountsBefore = userSteps.getUserAccounts(userAuthToken);
        var receiverAccountsBefore = userSteps.getUserAccounts(secondUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney, secondUserAccount, amount);

        this.transferAssertionSteps.assertInvalidTransfer(transferRequestBody, userAuthToken);

        var senderAccountsAfter = userSteps.getUserAccounts(userAuthToken);
        var receiverAccountsAfter = userSteps.getUserAccounts(secondUserAuthToken);

        this.accountAssertionSteps.assertBalanceWasNotChanged(
                senderAccountsBefore, senderAccountsAfter, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCantMakeTransferToOwnUserAccountWithValidAmountIfMoneyNotEnough(int amount) {
        var accountWithNoMoney = UserSteps.createAccount(userAuthToken);
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);

        var transferRequestBody =  TransferRequestGenerator.generate(accountWithNoMoney, firstUserSecondAccount, amount);
        this.transferAssertionSteps.assertInvalidTransfer(transferRequestBody, userAuthToken);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void checkUserCantMakeTransferWithTooLowAmount(int amount) {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);
        var transferRequestBody = TransferRequestGenerator.generate(firstUserFirstAccount, firstUserSecondAccount, amount);

        this.transferAssertionSteps.assertAmountIsMoreThanMinimum(transferRequestBody, userAuthToken);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserFirstAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    public void checkUserCantMakeTransferWithTooBigAmount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserFirstAccount, firstUserSecondAccount, 10_001);

        this.transferAssertionSteps.assertTransferAmountLessThanMaximum(transferRequestBody, userAuthToken);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserFirstAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    public void checkUserCantMakeTransferToNotExistedAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);

        var transferRequestBody = RequestDataGenerator.generateFilledObject(TransferRequestBody.class);
        transferRequestBody.setSenderAccountId(firstUserFirstAccount.getId());

        this.transferAssertionSteps.assertInvalidTransfer(transferRequestBody, userAuthToken);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserFirstAccount);
    }

    @Test
    public void checkUserCantMakeTransferFromNotExistedAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);

        var transferRequestBody = RequestDataGenerator.generateFilledObject(TransferRequestBody.class);
        transferRequestBody.setReceiverAccountId(firstUserSecondAccount.getId());

        new CrudRequester(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden(), Endpoint.TRANSFER).post(transferRequestBody);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }
}
