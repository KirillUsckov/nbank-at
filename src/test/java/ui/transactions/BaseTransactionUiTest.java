package ui.transactions;

import ui.BaseUiTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.AccountSteps;

public class BaseTransactionUiTest extends BaseUiTest {
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
