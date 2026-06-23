package ru.kduskov.ui.utils;

import lombok.experimental.UtilityClass;
import ru.kduskov.ui.models.UserModel;

@UtilityClass
public final class TestDataReader {
    private static final String TEST_DATA_FOLDER = "testdata";
    private static final String ADMIN_DATA_FILE = "admin.json";

    public static UserModel getAdmin() {
        var stringPath = String.join("/", TEST_DATA_FOLDER, ADMIN_DATA_FILE);
        return JsonParser.parse(stringPath, UserModel.class);
    }
}
