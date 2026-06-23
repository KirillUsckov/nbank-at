package ru.kduskov.mock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FraudStatus {
    APPROVED("mocks/fraud_approved.json", "Transfer approved and processed immediately"),
    MANUAL_REVIEW_REQUIRED("mocks/fraud_manual_review_required.json", "Transfer requires manual review"),
    VERIFICATION_REQUIRED("mocks/verification_required.json", "Additional verification required"),;
    @Getter
    private final String responseFile;
    @Getter
    private final String statusMessage;
}
