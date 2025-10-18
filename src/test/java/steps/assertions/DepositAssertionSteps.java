package steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.Assertions;
import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.steps.UserSteps;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferAssertionSteps {
    private final UserSteps userSteps;
    private final Assertions assertions;

    public TransferAssertionSteps(SoftAssertions softly, UserSteps userSteps) {
        this.assertions = new Assertions(softly);
        userSteps = userSteps;
    }

    public void assertTransferAmountLessThanMaximum(TransferRequestBody body, String userAuthToken) {
        var message = userSteps.getBadRequestTransferStringResponse(body, userAuthToken);
        assertEquals("Transfer amount cannot exceed 10000", message);
    }

    public void assertAmountIsMoreThanMinimum(TransferRequestBody body, String userAuthToken) {
        var message = userSteps.getBadRequestTransferStringResponse(body, userAuthToken);
        assertEquals("Transfer amount must be at least 0.01", message);
    }

    public void assertInvalidTransfer(TransferRequestBody body, String userAuthToken) {
        var message = userSteps.getBadRequestTransferStringResponse(body, userAuthToken);
        assertEquals("Invalid transfer: insufficient funds or invalid accounts", message);
    }

    public void assertTransferResponse(TransferRequestBody transferRequestBody, TransferResponseBody transferResponse) {
        assertions.assertThat(transferResponse)
                .matchesRequest(transferRequestBody);
    }
}
