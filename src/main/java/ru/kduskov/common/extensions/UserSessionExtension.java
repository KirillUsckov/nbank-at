package ru.kduskov.common.extensions;

import io.qameta.allure.Allure;
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
        var annotation = context.getRequiredTestMethod()
                .getAnnotation(UserSession.class);

        if (annotation == null) {
            return;
        }
        System.out.printf(
                "beforeEach: test=%s, thread=%s%n",
                context.getRequiredTestMethod().getName(),
                Thread.currentThread().getName()
        );

        Allure.step("Create user session", () ->
                createSession(annotation)
        );
    }

    private void createSession(UserSession annotation) {
        for (int i = 0; i < annotation.usersNumber(); i++) {
            var userModel = AdminSteps.createRandomUser();
            var accounts = new ArrayList<AccountResponseBody>();

            for (int j = 0; j < annotation.accountsNumber(); j++) {
                accounts.add(AccountSteps.createAccount(userModel.getToken()));
            }

            SessionStorage.insertUserAccounts(
                    userModel.getUsername(),
                    accounts
            );
            SessionStorage.insertUser(userModel);
        }

        if (annotation.isUi()) {
            var user = SessionStorage.getUser(annotation.userForLogin());
            loginWithUserCredentials(user.getToken());
        }
    }
}