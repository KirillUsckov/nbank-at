package ru.kduskov.steps;


import io.restassured.specification.ResponseSpecification;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.enums.Role;
import ru.kduskov.generators.RequestDataGenerator;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.request.CreateUserRequestBody;
import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

import java.util.List;

public class UserSteps {

    public String getChangeUserProfileStringResponse(ChangeUserProfileRequestBody body, String userAuthToken, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userSpec(userAuthToken), responseSpecification, Endpoint.CHANGE_USER_PROFILE)
                .put(body)
                .extract()
                .body()
                .asString();
    }

    public static String createRandomUser() {
        var requestBody = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        requestBody.setRole(Role.USER);
        return new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.CREATE_USER)
                .post(requestBody)
                .extract().header("Authorization");
    }

    public static AccountResponseBody createAccount(String userAuthToken) {
        return new ValidatedCrudRequested<AccountResponseBody>(
                RequestSpecs.userSpec(userAuthToken),
                ResponseSpecs.entityWasCreated(),
                Endpoint.CREATE_ACCOUNT)
                .post();
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
