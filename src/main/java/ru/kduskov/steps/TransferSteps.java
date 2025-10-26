package ru.kduskov.steps;

import io.restassured.specification.ResponseSpecification;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.specs.RequestSpecs;

public class TransferSteps {
    public String sendTransferRequestWithStringResponse(TransferRequestBody body, String userAuthToken, ResponseSpecification responseSpecification) {
        return new CrudRequester(RequestSpecs.userSpec(userAuthToken), responseSpecification, Endpoint.TRANSFER)
                .post(body)
                .extract()
                .body()
                .asString();
    }
}
