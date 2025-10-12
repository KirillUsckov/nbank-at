package tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.MakeDepositRequestBody;
import ru.kduskov.models.body.response.Account;
import ru.kduskov.models.body.response.Customer;
import ru.kduskov.requests.CreateAccountRequest;
import ru.kduskov.requests.GetUserProfileRequest;
import ru.kduskov.requests.MakeDepositRequest;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import ru.kduskov.utils.AccountsListUtils;
import steps.UserSteps;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MakeDepositTest extends BaseTest {
    private static Account firstUserAccount;
    private static Account secondUserAccount;
    private static String secondUserAuthToken;

    @BeforeAll
    public static void createAcc() {
        firstUserAccount = new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);

        secondUserAuthToken = UserSteps.createRandomUser();
        secondUserAccount = new CreateAccountRequest(RequestSpecs.userSpec(secondUserAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);
    }

    @Test
    public void checkAdminCannotMakeDeposit() {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        new MakeDepositRequest(RequestSpecs.adminSpec(), ResponseSpecs.accessForbidden())
                .post(
                        MakeDepositRequestBody.builder()
                                .id(firstUserAccount.getId())
                                .build()
                );

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);
        this.userSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }

    @Test
    public void checkUserCanMakeDepositToHisOwnAccount() {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        var depositResponseBody =
                new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .post(depositRequestBody).extract().as(Account.class);
        firstUserAccount.setBalance(firstUserAccount.getBalance() + depositRequestBody.getBalance());

        this.userSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount);

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);
        this.userSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                firstUserAccount,
                depositRequestBody.getBalance());
    }

    @Test
    public void checkUserCantMakeDepositToOthersAccount() {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(secondUserAuthToken);
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(secondUserAccount.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden())
                .post(depositRequestBody);
        var accountsAfterRequest = this.userSteps.getUserAccounts(secondUserAuthToken);
        this.userSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, secondUserAccount);
    }

    @Test
    public void checkUserCantMakeDepositToNotExistedAccount() {
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(RandomData.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden())
                .post(depositRequestBody);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4999, 5000})
    public void checkUserCanMakeDepositOnlyWithBalanceInRange(int balance) {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(balance)
                        .build();
        var depositResponseBody =
                new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .post(depositRequestBody).extract().as(Account.class);
        var totalBalance = firstUserAccount.getBalance() + balance;
        firstUserAccount.setBalance(totalBalance);
        this.userSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount);

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);
        this.userSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                firstUserAccount,
                depositRequestBody.getBalance());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 5001})
    public void checkUserCanNotMakeDepositOnlyWithBalanceOutOfRange(int balance) {
        var accountsBeforeRequest = this.userSteps.getUserAccounts(userAuthToken);
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(balance)
                        .build();
        new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .post(depositRequestBody);

        var accountsAfterRequest = this.userSteps.getUserAccounts(userAuthToken);
        this.userSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }
}

