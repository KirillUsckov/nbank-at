package api.transactions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.AccountSteps;
import api.BaseTest;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;

public class BaseTransactionTest extends BaseTest {

    @AfterAll
    public static void deleteAccounts() {
        for (var user : SessionStorage.getAllUsers()) {
            for(var account : SessionStorage.getUserSteps(user.getUsername()).getUserAccounts())
                AccountSteps.deleteAccount(user.getToken(), account.getId());
        }
    }
}
