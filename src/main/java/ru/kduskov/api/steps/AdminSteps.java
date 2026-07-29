package ru.kduskov.api.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.models.body.response.admin.FullUserProfileResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.ui.models.UserModel;

public final class AdminSteps {

    public static FullUserProfileResponseBody getAllUsers() {
        return new ValidatedCrudRequested<FullUserProfileResponseBody>(
                RequestSpecs.adminSpec(),
                ResponseSpecs.ok(),
                Endpoint.GET_ALL_USERS)
                .get();
    }

    public static ExtractableResponse<Response> deleteUser(
            RequestSpecification requestSpec,
            ResponseSpecification responseSpec,
            Long userId
    ) {
        return new CrudRequester(requestSpec, responseSpec, Endpoint.DELETE_USER)
                .delete(userId)
                .extract();
    }

    public static String createUser(RequestSpecification requestSpecs, ResponseSpecification responseSpecs, CreateUserRequestBody requestBody) {
        return new CrudRequester(requestSpecs, responseSpecs, Endpoint.CREATE_USER)
                .post(requestBody)
                .extract()
                .header("Authorization");
    }

    public static UserModel createRandomUser() {
        var requestBody = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        var authToken = createUser(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), requestBody);
        return UserModel.fromCreateUserRequest(requestBody, authToken);
    }
}
