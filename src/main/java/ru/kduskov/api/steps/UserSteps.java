package ru.kduskov.api.steps;

import io.qameta.allure.Step;
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

    @Step("Change user profile")
    public ChangeUserProfileResponseBody changeUserProfile(
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

    @Step("Get user profile")
    public UserProfileResponseBody getUserProfile() {
        return new ValidatedCrudRequested<UserProfileResponseBody>(
                RequestSpecs.userSpec(authToken),
                ResponseSpecs.ok(),
                Endpoint.GET_USER_PROFILE)
                .get();
    }

    @Step("Get user accounts")
    public CustomerAccountsResponseBody getUserAccounts() {
        return new ValidatedCrudRequested<CustomerAccountsResponseBody>(
                RequestSpecs.userSpec(authToken),
                ResponseSpecs.ok(),
                Endpoint.GET_CUSTOMER_ACCOUNTS)
                .get();

    }

    @Step("Get transactions for account {id}")
    public TransactionsResponseBody getAccountTransactions(Long id) {
        return new ValidatedCrudRequested<TransactionsResponseBody>(
                RequestSpecs.userSpec(authToken),
                ResponseSpecs.ok(),
                Endpoint.GET_ACCOUNT_TRANSACTIONS)
                .get(String.valueOf(id));

    }
}
