package ru.kduskov.steps;

import ru.kduskov.enums.Endpoint;
import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

public class TransferSteps {

    public String getBadRequestTransferStringResponse(TransferRequestBody body, String userAuthToken) {
        return new CrudRequester(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest(), Endpoint.TRANSFER)
                .post(body)
                .extract()
                .body()
                .asString();
    }
}
