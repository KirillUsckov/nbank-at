package ui.transactions;

import ui.BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.steps.AccountSteps;

public class BaseTransactionTest extends BaseTest {
    protected static AccountResponseBody userAccount;

    @BeforeAll
    public static void setUpAccounts() {
        userAccount = AccountSteps.createAccount(userToken);
    }

    @AfterAll
    public static void deleteAccounts() {
        AccountSteps.deleteAccount(userToken, userAccount.getId());
    }
}
