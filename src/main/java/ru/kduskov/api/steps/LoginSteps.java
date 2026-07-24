package ru.kduskov.api.steps;

import io.qameta.allure.Step;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.request.LoginRequestBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;

public final class LoginSteps {
    @Step("Login with user {requestBody.username}")
    public static String login(LoginRequestBody requestBody) {
        return new CrudRequester(RequestSpecs.unauthSpec(), ResponseSpecs.ok(), Endpoint.AUTH_LOGIN)
                .post(requestBody)
                .extract()
                .header("Authorization");
    }
}
