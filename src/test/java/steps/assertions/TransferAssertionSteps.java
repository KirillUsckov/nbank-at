package steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.Assertions;
import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.models.body.response.accounts.transfer.TransferResponseBody;

public class TransferAssertionSteps extends BaseAssertionsSteps {
    public TransferAssertionSteps(SoftAssertions softly) {
        super(softly);
    }


    public void assertTransferResponse(TransferRequestBody transferRequestBody, TransferResponseBody transferResponse) {
        assertions.assertThat(transferResponse)
                .matchesRequest(transferRequestBody);
    }
}
