package ru.kduskov.common.extensions;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.db.steps.SqlSteps;
import ru.kduskov.ui.models.UserModel;

public class ClearTestDataExtension implements AfterEachCallback {
    @Override
    public void afterEach(ExtensionContext context) {
        if (!SessionStorage.getAllUsers().isEmpty()) {
            SqlSteps.deleteAllUsers(SessionStorage.getAllUsers().stream().map(UserModel::getUsername).toList());
        }
        SessionStorage.clear();
    }
}
