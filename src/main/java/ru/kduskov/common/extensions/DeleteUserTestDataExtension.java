package ru.kduskov.common.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.kduskov.api.steps.AccountSteps;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.common.storage.SessionStorage;

public class DeleteUserTestDataExtension implements AfterTestExecutionCallback {
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        for (var user : SessionStorage.getAllUsers()) {
            for (var account : SessionStorage.getUserSteps(user.getUsername()).getUserAccounts())
                AccountSteps.deleteAccount(user.getToken(), account.getId());
        }
        for (var user : SessionStorage.getAllUsers()) {
            var id = new UserSteps(user.getToken()).getCustomer().getId();
            AdminSteps.deleteUser(id);
        }
        SessionStorage.clear();
    }
}
