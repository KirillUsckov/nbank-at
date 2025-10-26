package ru.kduskov.steps;


import io.restassured.specification.ResponseSpecification;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.enums.Role;
import ru.kduskov.generators.common.RequestDataGenerator;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.request.CreateUserRequestBody;
import ru.kduskov.models.body.response.accounts.DeleteAccountResponse;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

import java.util.List;

public class AccountSteps {
    public static AccountResponseBody createAccount(String userAuthToken) {
        return new ValidatedCrudRequested<AccountResponseBody>(
                RequestSpecs.userSpec(userAuthToken),
                ResponseSpecs.entityWasCreated(),
                Endpoint.CREATE_ACCOUNT)
                .post();
    }

    public static DeleteAccountResponse deleteAccount(String userAuthToken, long accountId) {
        return new ValidatedCrudRequested<DeleteAccountResponse>(
                RequestSpecs.userSpec(userAuthToken),
                ResponseSpecs.ok(),
                Endpoint.DELETE_ACCOUNT)
                .delete(accountId);
    }
}
