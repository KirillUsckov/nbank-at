package api.transactions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.steps.AccountSteps;
import api.BaseTest;

public class BaseTransactionTest extends BaseTest {
    protected static AccountResponseBody firstUserAccount;
    protected static AccountResponseBody secondUserAccount;

    @BeforeAll
    public static void setUpAccounts() {
        firstUserAccount = AccountSteps.createAccount(firstUserAuthToken);
        secondUserAccount = AccountSteps.createAccount(secondUserAuthToken);
    }

    @AfterAll
    public static void deleteAccounts() {
        AccountSteps.deleteAccount(firstUserAuthToken, firstUserAccount.getId());
        AccountSteps.deleteAccount(secondUserAuthToken, secondUserAccount.getId());
    }
}
