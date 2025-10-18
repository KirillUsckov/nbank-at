package steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.Assertions;
import ru.kduskov.models.body.request.DepositRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.steps.UserSteps;

public class DepositAssertionSteps {
    private final Assertions assertions;

    public DepositAssertionSteps(SoftAssertions softly) {
        this.assertions = new Assertions(softly);
    }

    public void assertSingleDeposit(DepositRequestBody depositRequestBody,
                                    AccountResponseBody depositResponseBody,
                                    AccountResponseBody userAccount) {

        assertions.assertThat(depositResponseBody)
                .isValidDepositResponse(depositRequestBody.getBalance(), depositRequestBody.getId(), userAccount);
    }

}
