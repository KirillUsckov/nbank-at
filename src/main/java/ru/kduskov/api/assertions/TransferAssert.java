package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.models.body.request.TransferRequestBody;

public class TransferAssert extends BaseAssert<TransferAssert, TransferResponseBody> {

    public TransferAssert(TransferResponseBody actual, SoftAssertions softly) {
        super(actual, TransferAssert.class, softly);
    }

    public static TransferAssert assertThat(TransferResponseBody actual, SoftAssertions softly) {
        return new TransferAssert(actual, softly);
    }

    public TransferAssert receiverAccountIdEquals(long expectedReceiverAccountId) {
        softly(() ->
                softly.assertThat(actual.getReceiverAccountId())
                        .withFailMessage("Expected receiver account id %s but was %s",
                                expectedReceiverAccountId, actual.getReceiverAccountId())
                        .isEqualTo(expectedReceiverAccountId)
        );
        return this;
    }

    public TransferAssert senderAccountIdEquals(long expectedSenderAccountId) {
        softly(() ->
                softly.assertThat(actual.getSenderAccountId())
                        .withFailMessage("Expected sender account id %s but was %s",
                                expectedSenderAccountId, actual.getSenderAccountId())
                        .isEqualTo(expectedSenderAccountId)
        );
        return this;
    }

    public TransferAssert amountEquals(double expectedAmount) {
        softly(() ->
                softly.assertThat(actual.getAmount())
                        .withFailMessage("Expected amount %s but was %s",
                                expectedAmount, actual.getAmount())
                        .isEqualTo(expectedAmount)
        );
        return this;
    }

    public TransferAssert messageEquals(String expectedMessage) {
        softly(() ->
                softly.assertThat(actual.getMessage())
                        .withFailMessage("Expected message '%s' but was '%s'",
                                expectedMessage, actual.getMessage())
                        .isEqualTo(expectedMessage)
        );
        return this;
    }

    public TransferAssert matchesRequest(TransferRequestBody request) {
        return receiverAccountIdEquals(request.getReceiverAccountId())
                        .senderAccountIdEquals(request.getSenderAccountId())
                        .amountEquals(request.getAmount())
                        .messageEquals("Transfer successful");
    }
}