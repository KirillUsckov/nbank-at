package ru.kduskov.api.steps;

import io.restassured.specification.ResponseSpecification;
import lombok.AllArgsConstructor;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.response.accounts.TransactionsResponseBody;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.api.models.body.response.customer.profile.CustomerAccountsResponseBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;

@AllArgsConstructor
public class UserSteps {
    private String authToken;

    public ChangeUserProfileResponseBody getChangeUserProfileStringResponse(
            ChangeUserProfileRequestBody body,
            ResponseSpecification responseSpecification
    ) {
        return new ValidatedCrudRequested<ChangeUserProfileResponseBody>(
                RequestSpecs.userSpec(authToken),
                responseSpecification,
                Endpoint.CHANGE_USER_PROFILE
        )
                .put(body);
    }

    public UserProfileResponseBody getCustomer() {
        return new ValidatedCrudRequested<UserProfileResponseBody>(
                RequestSpecs.userSpec(authToken),
                ResponseSpecs.ok(),
                Endpoint.GET_USER_PROFILE)
                .get();
    }

    public CustomerAccountsResponseBody getUserAccounts() {
        return new ValidatedCrudRequested<CustomerAccountsResponseBody>(
                RequestSpecs.userSpec(authToken),
                ResponseSpecs.ok(),
                Endpoint.GET_CUSTOMER_ACCOUNTS)
                .get();

    }

    public TransactionsResponseBody getAccountTransactions(Long id) {
        return new ValidatedCrudRequested<TransactionsResponseBody>(
                RequestSpecs.userSpec(authToken),
                ResponseSpecs.ok(),
                Endpoint.GET_ACCOUNT_TRANSACTIONS)
                .get(String.valueOf(id));

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
