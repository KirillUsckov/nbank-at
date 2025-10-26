package tests.transactions;

import ru.kduskov.constants.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.enums.TransactionType;
import ru.kduskov.generators.DepositRequestGenerator;
import ru.kduskov.models.body.request.DepositRequestBody;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import ru.kduskov.steps.DepositSteps;
import ru.kduskov.steps.assertions.AccountAssertionSteps;
import ru.kduskov.steps.assertions.DepositAssertionSteps;

import static ru.kduskov.constants.ErrorMessages.Account.UNAUTHORIZED_ACCESS_TO_ACCOUNT;

public class MakeDepositTest extends BaseTransactionTest {
    private final DepositSteps depositSteps = new DepositSteps();
    private DepositAssertionSteps depositAssertionSteps;
    private AccountAssertionSteps accountAssertionSteps;

    @BeforeEach
    public void initAssertionClasses() {
        this.depositAssertionSteps = new DepositAssertionSteps(softly);
        this.accountAssertionSteps = new AccountAssertionSteps(softly);
    }

    @Test
    public void checkAdminCannotMakeDeposit() {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId());

        this.depositSteps.sendDepositWithStringResponse(depositRequestBody, RequestSpecs.adminSpec(), ResponseSpecs.accessForbidden());

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }

    @Test
    public void checkUserCanMakeDepositToHisOwnAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId());

        var depositResponseBody = this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUserAuthToken), ResponseSpecs.ok());
        firstUserAccount.setBalance(firstUserAccount.getBalance() + depositRequestBody.getBalance());

        this.depositAssertionSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                firstUserAccount,
                depositRequestBody.getBalance());
    }

    @Test
    public void checkUserCantMakeDepositToOthersAccount() {
        var accountsBeforeRequest = userSteps.getUserAccounts(secondUserAuthToken);
        var depositRequestBody = DepositRequestGenerator.generate();
        depositRequestBody.setId(secondUserAccount.getId());

        var message = this.depositSteps.sendDepositWithStringResponse(depositRequestBody, firstUserAuthToken, ResponseSpecs.accessForbidden());
        this.accountAssertionSteps.assertMessage(UNAUTHORIZED_ACCESS_TO_ACCOUNT, message);

        var accountsAfterRequest = userSteps.getUserAccounts(secondUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, secondUserAccount);
    }

    @Test
    public void checkUserCantMakeDepositToNotExistedAccount() {
        var depositRequestBody = DepositRequestGenerator.generate();

        var message = this.depositSteps.sendDepositWithStringResponse(depositRequestBody, firstUserAuthToken, ResponseSpecs.accessForbidden());

        this.accountAssertionSteps.assertMessage(UNAUTHORIZED_ACCESS_TO_ACCOUNT, message);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1, 4999.99, 5000})
    public void checkUserCanMakeDepositOnlyWithBalanceInRange(double balance) {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId(), balance);
        var depositResponseBody =  this.depositSteps.sendDeposit(depositRequestBody, RequestSpecs.userSpec(firstUserAuthToken), ResponseSpecs.ok());

        var totalBalance = firstUserAccount.getBalance() + balance;
        firstUserAccount.setBalance(totalBalance);
        this.depositAssertionSteps.assertSingleDeposit(depositRequestBody, depositResponseBody, firstUserAccount);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasIncreased(
                accountsBeforeRequest,
                accountsAfterRequest,
                firstUserAccount,
                depositRequestBody.getBalance());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 0})
    public void checkUserCanNotMakeDepositWithLowBalance(double balance) {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);
        var depositRequestBody = DepositRequestGenerator.generate(firstUserAccount.getId(), balance);

        var message = this.depositSteps.sendDepositWithStringResponse(depositRequestBody, firstUserAuthToken, ResponseSpecs.badRequest());
        this.depositAssertionSteps.assertMessage(ErrorMessages.Deposit.DEPOSIT_AMOUNT_MUST_BE_AT_LEAST_MIN, message);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }

    @Test
    public void checkUserCanNotMakeDepositWithHighBalance() {
        var accountsBeforeRequest = userSteps.getUserAccounts(firstUserAuthToken);
        var depositRequestBody =
                DepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(5000.01)
                        .build();

        var message = this.depositSteps.sendDepositWithStringResponse(depositRequestBody, firstUserAuthToken, ResponseSpecs.badRequest());
        this.depositAssertionSteps.assertMessage(ErrorMessages.Deposit.DEPOSIT_AMOUNT_CANNOT_EXCEED_MAX, message);

        var accountsAfterRequest = userSteps.getUserAccounts(firstUserAuthToken);
        this.accountAssertionSteps.assertBalanceWasNotChanged(accountsBeforeRequest, accountsAfterRequest, firstUserAccount);
    }
}

