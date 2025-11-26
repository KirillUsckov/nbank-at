package ru.kduskov.api.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.request.DepositRequestBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

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
