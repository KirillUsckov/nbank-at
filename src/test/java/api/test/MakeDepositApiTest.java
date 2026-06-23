package api.test;

import support.ExpectedAccountState;
import support.TransactionTestData;
import common.BaseTest;
import ru.kduskov.api.constants.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.api.generators.DepositRequestGenerator;
import ru.kduskov.api.models.body.request.DepositRequestBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.api.steps.DepositSteps;
import ru.kduskov.api.steps.assertions.AccountAssertionSteps;
import ru.kduskov.api.steps.assertions.DepositAssertionSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.db.steps.DbAssertionSteps;
import ru.kduskov.db.steps.SqlSteps;
import ru.kduskov.ui.models.UserModel;


import static common.Constans.*;
import static ru.kduskov.api.constants.ErrorMessages.Account.UNAUTHORIZED_ACCESS_TO_ACCOUNT;

public class MakeDepositApiTest extends BaseTest {
    private final DepositSteps depositSteps = new DepositSteps();
    private DepositAssertionSteps depositAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;
    private DbAssertionSteps dbAssertionSteps;
    private AccountResponseBody firstUserAccount;
    private UserModel firstUser;

    @BeforeEach
    public void initAssertionClasses() {
        this.depositAssertionSteps = new DepositAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
        this.dbAssertionSteps = new DbAssertionSteps(softly);
    }

    public void setUpTestData() {
        var testData = TransactionTestData.getUserAccount(FIRST_USER_ID, FIRST_ACC_ID);
        firstUser = testData.getUser();
        firstUserAccount = testData.getAccount();
    }

    @Test
    @UserSession(accountsNumber = 1)
    public void checkAdminCannotMakeDeposit() {
        setUpTestData();
        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var dbAccountBeforeRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());

        var apiAccountsBeforeRequest = userSteps.getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId());

        this.depositSteps.sendDepositWithStringResponse(depositRequestBody, RequestSpecs.adminSpec(), ResponseSpecs.accessForbidden());

        var apiAccountsAfterRequest = SessionStorage.getUserSteps(FIRST_ACC_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(apiAccountsBeforeRequest, apiAccountsAfterRequest, firstUserAccount);

        var dbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(dbAccountAfterRequest, dbAccountBeforeRequest, true);
    }

    @Test
    @UserSession(accountsNumber = 1)
    public void checkUserCanMakeDepositToHisOwnAccount() {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_ACC_ID).getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId());
        var totalBalance = firstUserAccount.getBalance() + depositRequestBody.getAmount();
        var expectedAccountDao = ExpectedAccountState.withBalance(firstUserAccount, totalBalance);

        var depositResponseBody = this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.ok());
        firstUserAccount.setBalance(firstUserAccount.getBalance() + depositRequestBody.getAmount());

        var transfers = SessionStorage.getUserSteps(FIRST_ACC_ID).getAccountTransactions(firstUserAccount.getId());
        this.depositAssertionSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount, transfers);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_ACC_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest.getAccounts(),
                accountsAfterRequest.getAccounts(),
                firstUserAccount,
                depositRequestBody.getAmount());

        var accountDaoAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());

        this.dbAssertionSteps.assertAccountDaoEquals(accountDaoAfterRequest, expectedAccountDao, false);

    }

    @Test
    @UserSession(usersNumber = 2, accountsNumber = 1)
    public void checkUserCantMakeDepositToOthersAccount() {
        setUpTestData();
        var userSteps = SessionStorage.getUserSteps(SECOND_USER_ID);
        var dbAccountBeforeRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());

        var secondUser = SessionStorage.getUser(SECOND_USER_ID);
        var accountsBeforeRequest = userSteps.getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate();
        var secondUserAccount = SessionStorage.getUserAccount(secondUser.getUsername(), FIRST_ACC_ID);
        depositRequestBody.setAccountId(secondUserAccount.getId());

        var depositResponseBody = this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.accessForbidden());
        this.accountAssertionSteps.assertMessage(UNAUTHORIZED_ACCESS_TO_ACCOUNT, depositResponseBody.getMessage());

        var accountsAfterRequest = userSteps.getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, secondUserAccount);

        var dbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(dbAccountAfterRequest, dbAccountBeforeRequest,true);
    }

    @Test
    @UserSession(accountsNumber = 1)
    public void checkUserCantMakeDepositToNotExistedAccount() {
        setUpTestData();

        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var user = userSteps.getCustomer();
        var dbAccountsBeforeRequest = SqlSteps.findAllAccountsByCustomerId(user.getId());

        var depositRequestBody = DepositRequestGenerator.generate();

        var depositResponseBody = this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.accessForbidden());
        this.accountAssertionSteps.assertMessage(UNAUTHORIZED_ACCESS_TO_ACCOUNT, depositResponseBody.getMessage());

        var dbAccountsAfterRequest = SqlSteps.findAllAccountsByCustomerId(user.getId());
        this.dbAssertionSteps.assertAccountDaoListEquals(dbAccountsAfterRequest, dbAccountsBeforeRequest);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 1)
    @ValueSource(doubles = {0.1, 4999.99, 5000})
    public void checkUserCanMakeDepositOnlyWithBalanceInRange(double balance) {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId(), balance);

        var totalBalance = firstUserAccount.getBalance() + balance;
        var expectedAccountDao = ExpectedAccountState.withBalance(firstUserAccount, totalBalance);

        var depositResponseBody =  this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.ok());
        firstUserAccount.setBalance(totalBalance);

        var transfers = SessionStorage.getUserSteps(FIRST_ACC_ID).getAccountTransactions(firstUserAccount.getId());
        this.depositAssertionSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount, transfers);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest.getAccounts(),
                accountsAfterRequest.getAccounts(),
                firstUserAccount,
                depositRequestBody.getAmount());

        var accountDaoAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());

        this.dbAssertionSteps.assertAccountDaoEquals(accountDaoAfterRequest, expectedAccountDao, false);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 1)
    @ValueSource(doubles = {-0.01, 0})
    public void checkUserCanNotMakeDepositWithLowBalance(double balance) {
        setUpTestData();

        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var dbAccountBeforeRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());

        var accountsBeforeRequest = userSteps.getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId(), balance);

        var depositResponseBody = this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.badRequest());
        this.accountAssertionSteps.assertMessage(ErrorMessages.Deposit.INVALID_ACCOUNT_OR_AMOUNT, depositResponseBody.getMessage());

        var accountsAfterRequest = userSteps.getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);

        var dbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(dbAccountAfterRequest, dbAccountBeforeRequest, true);
    }

    @Test
    @UserSession(accountsNumber = 1)
    public void checkUserCanNotMakeDepositWithHighBalance() {
        setUpTestData();

        var userSteps = SessionStorage.getUserSteps(FIRST_USER_ID);
        var dbAccountBeforeRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());

        var accountsBeforeRequest = userSteps.getUserAccounts();
        var depositRequestBody =
                DepositRequestBody.builder()
                        .accountId(firstUserAccount.getId())
                        .amount(5000.01)
                        .build();

        var depositResponseBody = this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.badRequest());
        var message = depositResponseBody.getMessage();
        this.depositAssertionSteps.assertMessage(ErrorMessages.Deposit.DEPOSIT_AMOUNT_EXCEED_MAX, message);

        var accountsAfterRequest = userSteps.getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);

        var dbAccountAfterRequest = SqlSteps.getAccountByAccountNumber(firstUserAccount.getAccountNumber());
        this.dbAssertionSteps.assertAccountDaoEquals(dbAccountAfterRequest, dbAccountBeforeRequest, true);
    }
}

