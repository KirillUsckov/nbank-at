package ru.kduskov.common.extensions;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.kduskov.common.storage.SessionStorage;

public class ClearSessionStorageExtension implements AfterEachCallback {
    @Override
    public void afterEach(ExtensionContext context) {
        SessionStorage.clear();
    }
}
