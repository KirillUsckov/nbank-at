package ru.kduskov.api.generators;

import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.DepositRequestBody;

public class DepositRequestGenerator {
    public static DepositRequestBody generate() {
        return RequestDataGenerator.generateFilledObject(DepositRequestBody.class);
    }

    public static DepositRequestBody generate(Long accountId) {
        var request = generate();
        request.setAccountId(accountId);
        return request;
    }

    public static DepositRequestBody generate(Long accountID, double balance) {
        return DepositRequestBody.builder()
                .accountId(accountID)
                .amount(balance)
                .build();
    }
}
