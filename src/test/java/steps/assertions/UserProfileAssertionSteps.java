package steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.Assertions;
import ru.kduskov.models.body.request.DepositRequestBody;
import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.steps.UserSteps;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositAssertionSteps {
    private final UserSteps userSteps;
    private final Assertions assertions;

    public DepositAssertionSteps(SoftAssertions softly, UserSteps userSteps) {
        this.assertions = new Assertions(softly);
        this.userSteps = userSteps;
    }

    public void assertSingleDeposit(DepositRequestBody depositRequestBody,
                                    AccountResponseBody depositResponseBody,
                                    AccountResponseBody userAccount) {

        assertions.assertThat(depositResponseBody)
                .isValidDepositResponse(depositRequestBody.getBalance(), depositRequestBody.getId(), userAccount);
    }

}
