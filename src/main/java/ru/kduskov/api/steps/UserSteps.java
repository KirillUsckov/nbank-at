package ru.kduskov.api.steps;


import io.restassured.specification.ResponseSpecification;
import lombok.AllArgsConstructor;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.enums.Role;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.ui.models.UserModel;

import java.util.List;

@AllArgsConstructor
public class UserSteps {
    private String authToken;

    public String getChangeUserProfileStringResponse(ChangeUserProfileRequestBody body, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userSpec(authToken), responseSpecification, Endpoint.CHANGE_USER_PROFILE)
                .put(body)
                .extract()
                .body()
                .asString();
    }

    public UserProfileResponseBody getCustomer() {
        return new ValidatedCrudRequested<UserProfileResponseBody>(
                RequestSpecs.userSpec(authToken),
                ResponseSpecs.ok(),
                Endpoint.GET_USER_PROFILE)
                .get();
    }

    public List<AccountResponseBody> getUserAccounts() {
        return getCustomer().getAccounts();
    }

    public ChangeUserProfileResponseBody changeUserProfile(ChangeUserProfileRequestBody requestBody) {
        return new ValidatedCrudRequested<ChangeUserProfileResponseBody>(
                RequestSpecs.userSpec(authToken),
                ResponseSpecs.ok(),
                Endpoint.CHANGE_USER_PROFILE
        )
                .put(requestBody);
    }

}
