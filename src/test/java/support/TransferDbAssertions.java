package support;

import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.common.assertions.OptionalAssert;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.steps.DbAssertionSteps;
import ru.kduskov.db.steps.SqlSteps;

public final class TransferDbAssertions {
    private TransferDbAssertions() {
    }

    public static void assertTransferTransactionsPersisted(
            DbAssertionSteps dbAssertionSteps,
            TransferResponseBody transferResponse,
            Long senderTrxId,
            Long receiverTrxId) {
        var senderTransactionOpt = SqlSteps.findTransactionById(senderTrxId);
        var receiverTransactionOpt = SqlSteps.findTransactionById(receiverTrxId);
        OptionalAssert.assertThat(senderTransactionOpt).isPresent();
        OptionalAssert.assertThat(receiverTransactionOpt).isPresent();

        dbAssertionSteps.assertTransactionDaoEquals(
                senderTransactionOpt.get(),
                senderTrxId,
                transferResponse.getSenderAccountId(),
                transferResponse.getReceiverAccountId(),
                transferResponse.getAmount(),
                TransactionType.TRANSFER_OUT);
        dbAssertionSteps.assertTransactionDaoEquals(
                receiverTransactionOpt.get(),
                receiverTrxId,
                transferResponse.getReceiverAccountId(),
                transferResponse.getSenderAccountId(),
                transferResponse.getAmount(),
                TransactionType.TRANSFER_IN);
    }
}
