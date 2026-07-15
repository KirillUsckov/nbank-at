package ru.kduskov.api.generators;

import ru.kduskov.api.models.body.request.LoginRequestBody;
import ru.kduskov.api.models.body.response.admin.FullUserProfileInfo;

public final class LoginRequestGenerator {
    public static LoginRequestBody generate(FullUserProfileInfo user) {
        return LoginRequestBody.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
