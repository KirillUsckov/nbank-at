package tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.TransferMoneyRequestBody;
import ru.kduskov.models.body.response.Account;
import ru.kduskov.models.body.response.accounts.transfer.TransferMoneyResponseBody;
import ru.kduskov.requests.CreateAccountRequest;
import ru.kduskov.requests.TransferMoneyRequest;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import ru.kduskov.utils.AccountsListUtils;
import steps.UserSteps;

public class TransferMoneyTest extends BaseTest {
    private static Account firstUserFirstAccount;
    private static Account firstUserSecondAccount;
    private static Account secondUserAccount;
    private static String secondUserAuthToken;

    @BeforeAll
    public static void createAcc() {
        firstUserFirstAccount = UserSteps.createAccount(userAuthToken);
        UserSteps.makeDeposit(firstUserFirstAccount, userAuthToken, 50_000);
        firstUserSecondAccount = UserSteps.createAccount(userAuthToken);

        secondUserAuthToken = UserSteps.createRandomUser();
        secondUserAccount = UserSteps.createAccount(secondUserAuthToken);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCanMakeTransferToHisAnotherAccountWithValidAmountIfMoneyEnough(int amount) {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        var senderAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsBeforeRequest,
                firstUserFirstAccount);
        var receiverAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsBeforeRequest,
                firstUserSecondAccount);
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(amount)
                        .build();
        var depositResponseBody =
                new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .post(transferRequestBody).extract().as(TransferMoneyResponseBody.class);
        this.userSteps.assertTransferResponse(transferRequestBody, depositResponseBody);

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);
        var senderAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest,
                firstUserFirstAccount);
        var receiverAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsAfterRequest,
                firstUserSecondAccount);

        this.userSteps.assertBalanceForReceiverAndSenderAfterTransferWasChanged(
                senderAccountBeforeRequest,
                receiverAccountBeforeRequest,
                senderAccountAfterRequest,
                receiverAccountAfterRequest,
                amount
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCanMakeTransferToAnotherUserAccountWithValidAmountIfMoneyEnough(int amount) {
        var senderAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                this.userSteps.getUserAccounts(userAuthToken),
                firstUserFirstAccount);
        var receiverAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                this.userSteps.getUserAccounts(secondUserAuthToken),
                secondUserAccount);

        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(secondUserAccount.getId())
                        .amount(amount)
                        .build();
        var depositResponseBody =
                new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .post(transferRequestBody).extract().as(TransferMoneyResponseBody.class);
        this.userSteps.assertTransferResponse(transferRequestBody, depositResponseBody);

        var senderAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                this.userSteps.getUserAccounts(userAuthToken),
                firstUserFirstAccount);
        var receiverAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                this.userSteps.getUserAccounts(secondUserAuthToken),
                secondUserAccount);

        this.userSteps.assertBalanceForReceiverAndSenderAfterTransferWasChanged(
                senderAccountBeforeRequest,
                receiverAccountBeforeRequest,
                senderAccountAfterRequest,
                receiverAccountAfterRequest,
                amount
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCantMakeTransferToAnotherUserAccountWithValidAmountIfMoneyNotEnough(int amount) {
        var accountWithNoMoney = new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);

        var senderAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                this.userSteps.getUserAccounts(userAuthToken),
                accountWithNoMoney);
        var receiverAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                this.userSteps.getUserAccounts(secondUserAuthToken),
                secondUserAccount);

        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(accountWithNoMoney.getId())
                        .receiverAccountId(secondUserAccount.getId())
                        .amount(amount)
                        .build();
        this.userSteps.assertInvalidTransfer(transferRequestBody, userAuthToken);

        var senderAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                this.userSteps.getUserAccounts(userAuthToken),
                accountWithNoMoney);
        var receiverAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                this.userSteps.getUserAccounts(secondUserAuthToken),
                secondUserAccount);

        this.userSteps.assertBalanceForReceiverAndSenderAfterTransferWasNotChanged(
                senderAccountBeforeRequest,
                receiverAccountBeforeRequest,
                senderAccountAfterRequest,
                receiverAccountAfterRequest);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCantMakeTransferToOwnUserAccountWithValidAmountIfMoneyNotEnough(int amount) {
        var accountWithNoMoney = new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);

        var senderAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsBeforeRequest, accountWithNoMoney);
        var receiverAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsBeforeRequest, firstUserSecondAccount);

        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(accountWithNoMoney.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(amount)
                        .build();
        this.userSteps.assertInvalidTransfer(transferRequestBody, userAuthToken);

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);

        var senderAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsAfterRequest, accountWithNoMoney);
        var receiverAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsAfterRequest, firstUserSecondAccount);

        this.userSteps.assertBalanceForReceiverAndSenderAfterTransferWasNotChanged(
                senderAccountBeforeRequest,
                receiverAccountBeforeRequest,
                senderAccountAfterRequest,
                receiverAccountAfterRequest);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void checkUserCantMakeTransferWithTooLowAmount(int amount) {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        var senderAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsBeforeRequest,
                firstUserFirstAccount);
        var receiverAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsBeforeRequest,
                firstUserSecondAccount);

        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(amount)
                        .build();
        this.userSteps.assertAmountIsMoreThanMinimum(transferRequestBody, userAuthToken);

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);

        var senderAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsAfterRequest, firstUserFirstAccount);
        var receiverAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsAfterRequest, firstUserSecondAccount);

        this.userSteps.assertBalanceForReceiverAndSenderAfterTransferWasNotChanged(
                senderAccountBeforeRequest,
                receiverAccountBeforeRequest,
                senderAccountAfterRequest,
                receiverAccountAfterRequest);
    }

    @Test
    public void checkUserCantMakeTransferWithTooBogAmount() {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        var senderAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsBeforeRequest, firstUserFirstAccount);
        var receiverAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsBeforeRequest, firstUserSecondAccount);
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(1_0001)
                        .build();
        this.userSteps.assertTransferAmountLessThanMaximum(transferRequestBody, userAuthToken);
        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);

        var senderAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsAfterRequest, firstUserFirstAccount);
        var receiverAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsAfterRequest, firstUserSecondAccount);

        this.userSteps.assertBalanceForReceiverAndSenderAfterTransferWasNotChanged(
                senderAccountBeforeRequest,
                receiverAccountBeforeRequest,
                senderAccountAfterRequest,
                receiverAccountAfterRequest);
    }

    @Test
    public void checkUserCantMakeTransferToNotExistedAccount() {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        var senderAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsBeforeRequest,
                firstUserFirstAccount);

        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(RandomData.getId())
                        .amount(RandomData.getValidTransferAmount())
                        .build();
        this.userSteps.assertInvalidTransfer(transferRequestBody, userAuthToken);

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);
        var senderAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsAfterRequest, firstUserFirstAccount);

        softly.assertThat(senderAccountAfterRequest.getBalance())
                .withFailMessage("Senders user account balance was changed")
                .isEqualTo(senderAccountBeforeRequest.getBalance());
    }

    @Test
    public void checkUserCantMakeTransferFromNotExistedAccount() {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        var receiverAccountBeforeRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(
                accountsBeforeRequest,
                firstUserSecondAccount);

        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(RandomData.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(RandomData.getValidTransferAmount())
                        .build();

        new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden())
                .post(transferRequestBody);

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);
        var receiverAccountAfterRequest = AccountsListUtils.findAccountByAccountNumberOrElseThrow(accountsAfterRequest, firstUserSecondAccount);

        softly.assertThat(receiverAccountAfterRequest.getBalance())
                .withFailMessage("Receiver user account balance was changed")
                .isEqualTo(receiverAccountBeforeRequest.getBalance());
    }
}
