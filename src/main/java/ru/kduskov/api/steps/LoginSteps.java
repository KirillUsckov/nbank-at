package ru.kduskov.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.request.LoginRequestBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.specs.RequestSpecs;

public final class LoginSteps {
    @Step("Login with user {requestBody.username}")
    public static ExtractableResponse<Response> login(
            LoginRequestBody requestBody,
            ResponseSpecification responseSpecs
    ) {
        return new CrudRequester(RequestSpecs.unauthSpec(), responseSpecs, Endpoint.AUTH_LOGIN)
                .post(requestBody)
                .extract();
    }
}
