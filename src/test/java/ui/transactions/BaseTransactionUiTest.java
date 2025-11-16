package ui.transactions;

import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.common.storage.SessionStorage;
import ui.BaseUiTest;
import org.junit.jupiter.api.AfterAll;
import ru.kduskov.api.steps.AccountSteps;

public class BaseTransactionUiTest extends BaseUiTest {
    @AfterAll
    public static void deleteAccounts() {
        for (var user : SessionStorage.getAllUsers()) {
            for(var account : SessionStorage.getUserSteps(user.getUsername()).getUserAccounts())
                AccountSteps.deleteAccount(user.getToken(), account.getId());
        }
    }
}
