package ru.kduskov.api.steps;

import io.restassured.specification.ResponseSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.request.TransferRequestBody;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;

public final class TransferSteps {
    public static String sendTransferRequestWithStringResponse(String userAuthToken, TransferRequestBody body, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userSpec(userAuthToken), responseSpecification, Endpoint.TRANSFER)
                .post(body)
                .extract()
                .body()
                .asString();
    }

    public static TransferResponseBody sendTransferRequest(String userToken, TransferRequestBody transferReq, ResponseSpecification responseSpec) {
        return new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(userToken), responseSpec, Endpoint.TRANSFER)
                .post(transferReq);
    }

    public static TransferResponseBody sendTransferWithFraudRequest(String userToken, TransferRequestBody transferReq, ResponseSpecification responseSpec) {
        return new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(userToken), responseSpec, Endpoint.TRANSFER_WITH_FRAUD)
                .post(transferReq);
    }
}
