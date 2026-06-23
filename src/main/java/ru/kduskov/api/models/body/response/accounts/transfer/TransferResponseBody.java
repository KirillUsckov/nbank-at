package ru.kduskov.api.models.body.response.accounts.transfer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.mock.enums.FraudStatus;
import ru.kduskov.api.models.body.response.BaseResponse;

import java.math.BigDecimal;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransferResponseBody extends BaseResponse {
    private FraudStatus status;
    private double fraudRiskScore;
    private String fraudReason;
    private boolean requiresManualReview;
    private boolean requiresVerification;
    private Long senderAccountId;
    private Long transactionId;
    private Long receiverAccountId;
    private BigDecimal amount;

    public boolean getRequiresManualReview() {
        return requiresManualReview;
    }

    public boolean getRequiresVerification() {
        return requiresVerification;
    }
}
