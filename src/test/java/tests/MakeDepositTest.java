package tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.DepositRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import ru.kduskov.steps.UserSteps;
import steps.assertions.AccountAssertionSteps;
import steps.assertions.DepositAssertionSteps;
import steps.assertions.TransferAssertionSteps;

public class MakeDepositTest extends BaseTest {
    private static AccountResponseBody firstUserAccount;
    private static AccountResponseBody secondUserAccount;
    private static String secondUserAuthToken;
    private DepositAssertionSteps depositAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;

    @BeforeAll
    public static void createAcc() {
        firstUserAccount =
                new ValidatedCrudRequested<AccountResponseBody>(
                        RequestSpecs.userSpec(userAuthToken),
                        ResponseSpecs.entityWasCreated(),
                        Endpoint.CREATE_ACCOUNT
                )
                        .post();

        secondUserAuthToken = UserSteps.createRandomUser();
        secondUserAccount = new ValidatedCrudRequested<AccountResponseBody>(
                RequestSpecs.userSpec(secondUserAuthToken),
                ResponseSpecs.entityWasCreated(),
                Endpoint.CREATE_ACCOUNT
        )
                .post();
    }

    @BeforeEach
    public void initAssertionClasses() {
        this.depositAssertionSteps = new DepositAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @Test
    public void checkAdminCannotMakeDeposit() {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);
        new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.accessForbidden(), Endpoint.MAKE_DEPOSIT)
                .post(
                        DepositRequestBody.builder()
                                .id(firstUserAccount.getId())
                                .build()
                );

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }

    @Test
    public void checkUserCanMakeDepositToHisOwnAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);
        var depositRequestBody =
                DepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        var depositResponseBody =
                new ValidatedCrudRequested<AccountResponseBody>(
                        RequestSpecs.userSpec(userAuthToken),
                        ResponseSpecs.ok(),
                        Endpoint.MAKE_DEPOSIT
                )
                        .post(depositRequestBody);
        firstUserAccount.setBalance(firstUserAccount.getBalance() + depositRequestBody.getBalance());

        this.depositAssertionSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                firstUserAccount,
                depositRequestBody.getBalance());
    }

    @Test
    public void checkUserCantMakeDepositToOthersAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(secondUserAuthToken);
        var depositRequestBody =
                DepositRequestBody.builder()
                        .id(secondUserAccount.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        new CrudRequester(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden(), Endpoint.MAKE_DEPOSIT)
                .post(depositRequestBody);
        var accountsAfterRequest = userSteps.getUserAccounts(secondUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, secondUserAccount);
    }

    @Test
    public void checkUserCantMakeDepositToNotExistedAccount() {
        var depositRequestBody =
                DepositRequestBody.builder()
                        .id(RandomData.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        new CrudRequester(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden(), Endpoint.MAKE_DEPOSIT).post(depositRequestBody);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4999, 5000})
    public void checkUserCanMakeDepositOnlyWithBalanceInRange(int balance) {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);
        var depositRequestBody =
                DepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(balance)
                        .build();
        var depositResponseBody =
                new ValidatedCrudRequested<AccountResponseBody>(
                        RequestSpecs.userSpec(userAuthToken),
                        ResponseSpecs.ok(),
                        Endpoint.MAKE_DEPOSIT
                )
                        .post(depositRequestBody);
        var totalBalance = firstUserAccount.getBalance() + balance;
        firstUserAccount.setBalance(totalBalance);
        this.depositAssertionSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                firstUserAccount,
                depositRequestBody.getBalance());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 5001})
    public void checkUserCanNotMakeDepositOnlyWithBalanceOutOfRange(int balance) {
        var accountsBeforeRequest = userSteps.getUserAccounts(userAuthToken);
        var depositRequestBody =
                DepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(balance)
                        .build();
        new CrudRequester(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest(), Endpoint.MAKE_DEPOSIT).post(depositRequestBody);

        var accountsAfterRequest = userSteps.getUserAccounts(userAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }
}

