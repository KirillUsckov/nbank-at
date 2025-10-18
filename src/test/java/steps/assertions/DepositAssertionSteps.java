package steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.Assertions;
import ru.kduskov.models.body.request.DepositRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositAssertionSteps extends BaseAssertionsSteps {

    public DepositAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    public void assertSingleDeposit(DepositRequestBody depositRequestBody,
                                    AccountResponseBody depositResponseBody,
                                    AccountResponseBody userAccount) {

        assertions.assertThat(depositResponseBody)
                .isValidDepositResponse(depositRequestBody.getBalance(), depositRequestBody.getId(), userAccount);
    }
}
