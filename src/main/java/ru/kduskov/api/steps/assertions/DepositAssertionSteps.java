package ru.kduskov.api.steps.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.request.DepositRequestBody;
import ru.kduskov.api.models.body.response.Transaction;
import ru.kduskov.api.models.body.response.accounts.TransactionsResponseBody;
import ru.kduskov.api.models.body.response.accounts.deposit.DepositResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.utils.TransactionsListUtils;
import ru.kduskov.common.steps.BaseAssertionsSteps;

public class DepositAssertionSteps extends BaseAssertionsSteps {
    public DepositAssertionSteps(SoftAssertions softly) {
        super(softly);
    }

    public void assertSingleDeposit(DepositRequestBody depositRequestBody,
                                    DepositResponseBody depositResponseBody,
                                    AccountResponseBody userAccount,
                                    TransactionsResponseBody transactionsResponseBody) {

        assertions.assertThat(depositResponseBody)
                .isValidDepositResponse(depositRequestBody, userAccount);
        assertions.assertThat(transactionsResponseBody).hasTransactions();

        Transaction latestTransaction = TransactionsListUtils.findLatestTransaction(transactionsResponseBody.getTransactions());

        assertions.assertThat(latestTransaction)
                .matches(depositRequestBody.getAmount(), TransactionType.DEPOSIT, userAccount.getId());

    }
}
