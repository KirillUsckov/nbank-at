package ru.kduskov.api.steps;

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

    public static String deleteUser(long userId) {
        return new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.ok(), Endpoint.DELETE_USER)
                .delete(userId)
                .extract()
                .asString();
    }

    public static String createUser(CreateUserRequestBody requestBody) {
        return new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.CREATE_USER)
                .post(requestBody)
                .extract()
                .header("Authorization");
    }

    public static UserModel createRandomUser() {
        var requestBody = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);

        System.out.printf(
                "Created user: %s, thread=%s%n",
                requestBody.getUsername(),
                Thread.currentThread().getName()
        );
        var authToken = createUser(requestBody);
        return UserModel.fromCreateUserRequest(requestBody, authToken);
    }
}
