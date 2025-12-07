package api.transactions;

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
import ru.kduskov.ui.models.UserModel;

import static common.Constans.*;
import static ru.kduskov.api.constants.ErrorMessages.Account.UNAUTHORIZED_ACCESS_TO_ACCOUNT;

public class MakeDepositApiTest extends BaseTest {
    private final DepositSteps depositSteps = new DepositSteps();
    private DepositAssertionSteps depositAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;
    private AccountResponseBody firstUserAccount;
    private UserModel firstUser;

    @BeforeEach
    public void initAssertionClasses() {
        this.depositAssertionSteps = new DepositAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    public void setUpTestData() {
        firstUser = SessionStorage.getUser(FIRST_USER_ID);
        firstUserAccount = SessionStorage.getUserAccount(firstUser.getUsername(), FIRST_ACC_ID);
    }

    @Test
    @UserSession(accountsNumber = 1)
    public void checkAdminCannotMakeDeposit() {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_ACC_ID).getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId());

        this.depositSteps.sendDepositWithStringResponse(depositRequestBody, RequestSpecs.adminSpec(), ResponseSpecs.accessForbidden());

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_ACC_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }

    @Test
    @UserSession(accountsNumber = 1)
    public void checkUserCanMakeDepositToHisOwnAccount() {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_ACC_ID).getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId());

        var depositResponseBody = this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.ok());
        firstUserAccount.setBalance(firstUserAccount.getBalance() + depositRequestBody.getBalance());

        this.depositAssertionSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_ACC_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                firstUserAccount,
                depositRequestBody.getBalance());
    }

    @Test
    @UserSession(usersNumber = 2, accountsNumber = 1)
    public void checkUserCantMakeDepositToOthersAccount() {
        setUpTestData();
        var secondUser = SessionStorage.getUser(SECOND_USER_ID);
        var accountsBeforeRequest = SessionStorage.getUserSteps(SECOND_USER_ID).getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate();
        var secondUserAccount = SessionStorage.getUserAccount(secondUser.getUsername(), FIRST_ACC_ID);
        depositRequestBody.setId(secondUserAccount.getId());

        var message = this.depositSteps.sendDepositWithStringResponse(depositRequestBody, firstUser.getToken(), ResponseSpecs.accessForbidden());
        this.accountAssertionSteps.assertMessage(UNAUTHORIZED_ACCESS_TO_ACCOUNT, message);

        var accountsAfterRequest = SessionStorage.getUserSteps(SECOND_ACC_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, secondUserAccount);
    }

    @Test
    @UserSession(accountsNumber = 1)
    public void checkUserCantMakeDepositToNotExistedAccount() {
        setUpTestData();
        var depositRequestBody = DepositRequestGenerator.generate();

        var message = this.depositSteps.sendDepositWithStringResponse(depositRequestBody, firstUser.getToken(), ResponseSpecs.accessForbidden());

        this.accountAssertionSteps.assertMessage(UNAUTHORIZED_ACCESS_TO_ACCOUNT, message);
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 1)
    @ValueSource(doubles = {0.1, 4999.99, 5000})
    public void checkUserCanMakeDepositOnlyWithBalanceInRange(double balance) {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId(), balance);
        var depositResponseBody =  this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUser.getToken()), ResponseSpecs.ok());

        var totalBalance = firstUserAccount.getBalance() + balance;
        firstUserAccount.setBalance(totalBalance);
        this.depositAssertionSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                firstUserAccount,
                depositRequestBody.getBalance());
    }

    @ParameterizedTest
    @UserSession(accountsNumber = 1)
    @ValueSource(doubles = {-0.01, 0})
    public void checkUserCanNotMakeDepositWithLowBalance(double balance) {
        setUpTestData();
        var accountsBeforeRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId(), balance);

        var message = this.depositSteps.sendDepositWithStringResponse(depositRequestBody, firstUser.getToken(), ResponseSpecs.badRequest());
        this.depositAssertionSteps.assertMessage(ErrorMessages.Deposit.DEPOSIT_AMOUNT_MUST_BE_AT_LEAST_MIN, message);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }

    @Test
    @UserSession(accountsNumber = 1)
    public void checkUserCanNotMakeDepositWithHighBalance() {
        setUpTestData();
        var accountsBeforeRequest =SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        var depositRequestBody =
                DepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(5000.01)
                        .build();

        var message = this.depositSteps.sendDepositWithStringResponse(depositRequestBody, firstUser.getToken(), ResponseSpecs.badRequest());
        this.depositAssertionSteps.assertMessage(ErrorMessages.Deposit.DEPOSIT_AMOUNT_CANNOT_EXCEED_MAX, message);

        var accountsAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getUserAccounts();
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }
}

