package ru.kduskov.mock.models;

import ru.kduskov.mock.enums.FraudStatus;

public record FraudMockResponse(
        String status,
        FraudStatus decision,
        double riskScore,
        String reason,
        boolean requiresManualReview,
        boolean additionalVerificationRequired) {
}
