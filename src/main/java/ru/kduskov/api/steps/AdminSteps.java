package ru.kduskov.api.steps;

import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.ui.models.UserModel;

import java.util.List;

public final class AdminSteps {

    public static List<UserProfileResponseBody> getAllUsers() {
        return new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.ok(), Endpoint.GET_ALL_USERS)
                .get()
                .extract()
                .jsonPath()
                .getList("", UserProfileResponseBody.class);
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

        var authToken = createUser(requestBody);
        return UserModel.fromCreateUserRequest(requestBody, authToken);
    }
}
