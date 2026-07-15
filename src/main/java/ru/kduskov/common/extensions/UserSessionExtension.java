package ru.kduskov.common.extensions;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.steps.AccountSteps;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.storage.SessionStorage;

import java.util.ArrayList;

import static ru.kduskov.ui.pages.BasePage.loginWithUserCredentials;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) {
        var annotation = context.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            for(int i = 0; i < annotation.usersNumber(); i++) {
                var userModel = AdminSteps.createRandomUser();
                var accounts = new ArrayList<AccountResponseBody>();
                for (int j = 0; j < annotation.accountsNumber(); j++) {
                    accounts.add(AccountSteps.createAccount(userModel.getToken()));
                }
                SessionStorage.insertUserAccounts(userModel.getUsername(), accounts);
                SessionStorage.insertUser(userModel);
            }
            if(annotation.isUi()) {
                loginWithUserCredentials(SessionStorage.getUser(annotation.userForLogin()).getToken());
            }
        }
    }
}
