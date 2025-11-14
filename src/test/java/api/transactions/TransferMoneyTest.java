package api.transactions;

import ru.kduskov.api.constants.ErrorMessages;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

public class TransferMoneyTest extends BaseTransactionTest {
    private static AccountResponseBody firstUserSecondAccount;
    private final TransferSteps transferSteps = new TransferSteps();
    private TransferAssertionSteps transferAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;

    @BeforeAll
    public static void createAccounts() {
        DepositSteps.sendDepositWithAmountValidation(firstUserAccount, firstUserAuthToken, 50_000);
        firstUserSecondAccount = AccountSteps.createAccount(firstUserAuthToken);
    }
    
    @AfterAll
    public static void deleteAdditionalAccounts() {
        AccountSteps.deleteAccount(firstUserAuthToken, firstUserSecondAccount.getId());
    }

    @BeforeEach
    public void initAssertionClasses() {
        this.transferAssertionSteps = new TransferAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCanMakeTransferToHisAnotherAccountWithValidAmountIfMoneyEnough(double amount) {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);
        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);

        var transferResponse = new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(firstUserAuthToken), ResponseSpecs.ok(), Endpoint.TRANSFER)
                .post(transferRequestBody);

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
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
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCanMakeTransferToAnotherUserAccountWithValidAmountIfMoneyEnough(double amount) {
        var senderAccountsBefore = userSteps.getUserAccounts(firstUserAuthToken);
        var receiverAccountsBefore = userSteps.getUserAccounts(secondUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), secondUserAccount.getId(), amount);

        var transferResponse = new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(firstUserAuthToken), ResponseSpecs.ok(), Endpoint.TRANSFER)
                .post(transferRequestBody);

        this.transferAssertionSteps.assertTransferResponse(transferRequestBody, transferResponse);

        var senderAccountsAfter = userSteps.getUserAccounts(firstUserAuthToken);
        var receiverAccountsAfter = userSteps.getUserAccounts(secondUserAuthToken);

        this.accountAssertionSteps.assertBalanceWasIncreased(
                senderAccountsBefore, senderAccountsAfter, firstUserAccount, -amount);
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
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCantMakeTransferToAnotherUserAccountWithValidAmountIfMoneyNotEnough(double amount) {
        var accountWithNoMoney = AccountSteps.createAccount(firstUserAuthToken);
        var senderAccountsBefore = userSteps.getUserAccounts(firstUserAuthToken);
        var receiverAccountsBefore = userSteps.getUserAccounts(secondUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney.getId(), secondUserAccount.getId(), amount);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, firstUserAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var senderAccountsAfter = userSteps.getUserAccounts(firstUserAuthToken);
        var receiverAccountsAfter = userSteps.getUserAccounts(secondUserAuthToken);

        this.accountAssertionSteps.assertBalanceWasNotChanged(
                senderAccountsBefore, senderAccountsAfter, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                receiverAccountsBefore, receiverAccountsAfter, secondUserAccount);

        this.accountAssertionSteps.assertAccountHasNoTransactionsWithType(accountWithNoMoney, TransactionType.TRANSFER_OUT);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10_000})
    public void checkUserCantMakeTransferToOwnUserAccountWithValidAmountIfMoneyNotEnough(double amount) {
        var accountWithNoMoney = AccountSteps.createAccount(firstUserAuthToken);
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generate(accountWithNoMoney.getId(), firstUserSecondAccount.getId(), amount);
        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, firstUserAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, accountWithNoMoney);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);

        this.accountAssertionSteps.assertAccountHasNoTransactionsWithType(accountWithNoMoney, TransactionType.TRANSFER_OUT);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 0})
    public void checkUserCantMakeTransferWithTooLowAmount(double amount) {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);
        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), amount);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, firstUserAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.TRANSFER_AMOUNT_MUST_BE_AT_LEAST_MIN, message);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    public void checkUserCantMakeTransferWithTooBigAmount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), firstUserSecondAccount.getId(), 10_000.01);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, firstUserAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.TRANSFER_AMOUNT_CANNOT_EXCEED_MAX, message);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }

    @Test
    public void checkUserCantMakeTransferToNotExistedAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generate(firstUserAccount.getId(), 100000000L);

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, firstUserAuthToken, ResponseSpecs.badRequest());
        this.transferAssertionSteps.assertMessage(ErrorMessages.Transfer.INVALID_TRANSFER_INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS, message);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }

    @Test
    public void checkUserCantMakeTransferFromNotExistedAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);

        var transferRequestBody = TransferRequestGenerator.generateWithReceiver(firstUserSecondAccount.getId());

        var message = this.transferSteps.sendTransferRequestWithStringResponse(transferRequestBody, firstUserAuthToken, ResponseSpecs.accessForbidden());
        this.accountAssertionSteps.assertMessage(ErrorMessages.Account.UNAUTHORIZED_ACCESS_TO_ACCOUNT, message);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(
                accountsBeforeRequest, accountsAfterRequest, firstUserSecondAccount);
    }
}
