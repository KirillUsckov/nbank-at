package ru.kduskov.api.steps;

import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.response.accounts.DeleteAccountResponse;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;

public final class AccountSteps {
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
