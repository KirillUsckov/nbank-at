package ru.kduskov.steps;


import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.enums.Role;
import ru.kduskov.generators.common.RequestDataGenerator;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.request.CreateUserRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

import java.util.List;

public class UserSteps {
    public String createUser(CreateUserRequestBody requestBody) {
        return new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.CREATE_USER)
                .post(requestBody)
                .extract()
                .header("Authorization");
    }
    public String createRandomUser() {
        var requestBody = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        requestBody.setRole(Role.USER);
        return this.createUser(requestBody);
    }

    public List<UserProfileResponseBody> getAllUsers() {
        return new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.ok(), Endpoint.GET_ALL_USERS)
                .get()
                .extract()
                .jsonPath()
                .getList("", UserProfileResponseBody.class);
    }

    public String deleteUser(long userId) {
        return new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.ok(), Endpoint.DELETE_USER)
                .delete(userId)
                .extract()
                .asString();
    }

    public String getChangeUserProfileStringResponse(ChangeUserProfileRequestBody body, String userAuthToken, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userSpec(userAuthToken), responseSpecification, Endpoint.CHANGE_USER_PROFILE)
                .put(body)
                .extract()
                .body()
                .asString();
    }

    public UserProfileResponseBody getCustomer(String userAuthToken) {
        return new ValidatedCrudRequested<UserProfileResponseBody>(
                RequestSpecs.userSpec(userAuthToken),
                ResponseSpecs.ok(),
                Endpoint.GET_USER_PROFILE)
                .get();
    }

    public List<AccountResponseBody> getUserAccounts(String userAuthToken) {
        return this.getCustomer(userAuthToken).getAccounts();
    }
}
