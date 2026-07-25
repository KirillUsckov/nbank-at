package ru.kduskov.api.generators;

import ru.kduskov.api.models.body.request.LoginRequestBody;
import ru.kduskov.api.models.body.response.admin.FullUserProfileInfo;
import ru.kduskov.ui.models.UserModel;

public final class LoginRequestGenerator {
    public static LoginRequestBody generate(String username, String password) {
        return LoginRequestBody.builder()
                .username(username)
                .password(password)
                .build();
    }
}
