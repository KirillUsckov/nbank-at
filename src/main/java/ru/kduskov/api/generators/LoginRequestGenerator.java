package ru.kduskov.api.generators;

import ru.kduskov.api.models.body.request.LoginRequestBody;

public final class LoginRequestGenerator {
    public static LoginRequestBody generate(String username, String password) {
        return LoginRequestBody.builder()
                .username(username)
                .password(password)
                .build();
    }
}
