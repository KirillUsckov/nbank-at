package support;

import lombok.Getter;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.DepositSteps;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.ui.models.UserModel;

public class TransactionTestData {
    @Getter private final UserModel user;

    @Getter private final AccountResponseBody account;

    private TransactionTestData(UserModel user, AccountResponseBody account) {
        this.user = user;
        this.account = account;
    }
    public static TransactionTestData getAccountWithDeposit(int userId, int accountId, double amount) {
        var testData = getUserAccount(userId, accountId);
        DepositSteps.sendDepositWithAmountValidation(testData.account, testData.user.getToken(), amount);
        return testData;
    }

    public static TransactionTestData getUserAccount(int userId, int accountId) {
        var user = SessionStorage.getUser(userId);
        var account = SessionStorage.getUserAccount(user.getUsername(), accountId);
        return new TransactionTestData(user, account);
    }
}
