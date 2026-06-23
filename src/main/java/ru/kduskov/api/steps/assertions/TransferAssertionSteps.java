package ru.kduskov.api.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.request.TransferRequestBody;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.mock.models.FraudMockResponse;

public class TransferAssertionSteps extends BaseAssertionsSteps {
    public TransferAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    public void assertTransferResponse(TransferRequestBody transferRequestBody, TransferResponseBody transferResponse, String statusMessage) {
        assertions.assertThat(transferResponse)
                .matchesRequest(transferRequestBody, statusMessage);
    }

    public void assertTransferResponse(TransferRequestBody transferRequestBody, TransferResponseBody transferResponse, FraudMockResponse fraudMockResponse, String statusMessage) {
        assertions.assertThat(transferResponse)
                .matchesRequest(transferRequestBody, statusMessage);
    }
}
