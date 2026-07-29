package ru.kduskov.api.steps;

import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.request.TransferRequestBody;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.api.specs.RequestSpecs;

public final class TransferSteps {
    @Step("Send transfer")
    public static TransferResponseBody sendTransferRequest(
            String userToken,
            TransferRequestBody transferReq,
            ResponseSpecification responseSpec
    ) {
        return new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(userToken), responseSpec, Endpoint.TRANSFER
        )
                .post(transferReq);
    }

    @Step("Send transfer")
    public static String sendTransferRequestWithStringResponse(
            RequestSpecification requestSpec,
            TransferRequestBody transferReq,
            ResponseSpecification responseSpec
    ) {
        return new CrudRequester(
                requestSpec, responseSpec, Endpoint.TRANSFER
        )
                .post(transferReq)
                .extract()
                .asString();
    }

    @Step("Send transfer with fraud check")
    public static TransferResponseBody sendTransferWithFraudRequest(
            String userToken,
            TransferRequestBody transferReq,
            ResponseSpecification responseSpec
    ) {
        return new ValidatedCrudRequested<TransferResponseBody>(
                RequestSpecs.userSpec(userToken), responseSpec, Endpoint.TRANSFER_WITH_FRAUD)
                .post(transferReq);
    }

    @Step("Send transfer with fraud check")
    public static String sendTransferWithFraudRequestWithStringResponse(
            RequestSpecification requestSpec,
            TransferRequestBody transferReq,
            ResponseSpecification responseSpec
    ) {
        return new CrudRequester(
                requestSpec, responseSpec, Endpoint.TRANSFER_WITH_FRAUD
        )
                .post(transferReq)
                .extract()
                .asString();
    }
}
