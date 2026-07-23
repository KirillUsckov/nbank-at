package ru.kduskov.api.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.models.body.request.TransferRequestBody;
import ru.kduskov.mock.enums.FraudStatus;
import ru.kduskov.mock.models.FraudMockResponse;

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
                        .withFailMessage(
                                "Expected receiver account id %s but was %s",
                                expectedReceiverAccountId, actual.getReceiverAccountId())
                        .isEqualTo(expectedReceiverAccountId)
        );
        return this;
    }

    public TransferAssert senderAccountIdEquals(long expectedSenderAccountId) {
        isEqualTo(actual.getSenderAccountId(), expectedSenderAccountId,
                String.format("Expected sender account id %s but was %s",
                        expectedSenderAccountId, actual.getSenderAccountId())
        );
        return this;
    }

    public TransferAssert statusEquals(FraudStatus expectedStatus) {
        isEqualTo(actual.getStatus(), expectedStatus,
                String.format("Expected status %s but was %s", expectedStatus, actual.getStatus())
        );
        return this;
    }

    public TransferAssert fraudReasonEquals(String expectedFraudReason) {
        isEqualTo(actual.getFraudReason(), expectedFraudReason,
                String.format("Expected fraudReason %s but was %s", expectedFraudReason, actual.getFraudReason())
        );
        return this;
    }

    public TransferAssert requiresVerificationEquals(boolean expectedRequiresVerification) {
        isEqualTo(actual.getRequiresVerification(), expectedRequiresVerification,
                String.format("Expected requiresVerification %s but was %s", expectedRequiresVerification, actual.getRequiresVerification())
        );
        return this;
    }

    public TransferAssert requiresManualReviewEquals(boolean expectedRequiresManualReview) {
        isEqualTo(actual.getRequiresManualReview(), expectedRequiresManualReview,
                String.format("Expected requiresManualReview %s but was %s", expectedRequiresManualReview, actual.getRequiresManualReview())
        );
        return this;
    }

    public TransferAssert fraudRiskScoreEquals(double expectedFraudRiskScore) {
        isEqualTo(actual.getFraudRiskScore(), expectedFraudRiskScore,
                String.format("Expected fraudRiskScore %s but was %s", expectedFraudRiskScore, actual.getFraudRiskScore())
        );
        return this;
    }

    public TransferAssert amountEquals(double expectedAmount) {
        softly(() ->
                softly.assertThat(actual.getAmount().doubleValue())
                        .withFailMessage("Expected amount %s but was %s",
                                expectedAmount, actual.getAmount().doubleValue())
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

    public TransferAssert matchesRequest(TransferRequestBody request, String statusMessage) {
        return receiverAccountIdEquals(request.getReceiverAccountId())
                .senderAccountIdEquals(request.getSenderAccountId())
                .amountEquals(request.getAmount())
                .messageEquals(statusMessage);
    }

    public TransferAssert matchesRequest(TransferRequestBody request, FraudMockResponse fraudResponse, String statusMessage) {
        return matchesRequest(request, statusMessage)
                .statusEquals(fraudResponse.decision())
                .fraudRiskScoreEquals(fraudResponse.riskScore())
                .fraudReasonEquals(fraudResponse.reason())
                .requiresVerificationEquals(fraudResponse.additionalVerificationRequired())
                .requiresManualReviewEquals(fraudResponse.requiresManualReview());
    }
}