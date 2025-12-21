package ru.kduskov.api.steps;

import io.restassured.specification.ResponseSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.request.TransferRequestBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.specs.RequestSpecs;

public final class TransferSteps {
    public static String sendTransferRequestWithStringResponse(String userAuthToken, TransferRequestBody body, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userSpec(userAuthToken), responseSpecification, Endpoint.TRANSFER)
                .post(body)
                .extract()
                .body()
                .asString();
    }
}
