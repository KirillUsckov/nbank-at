package ru.kduskov.generators;

import ru.kduskov.models.body.request.DepositRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;

public class DepositRequestGenerator {
    public static DepositRequestBody generate() {
        return RequestDataGenerator.generateFilledObject(DepositRequestBody.class);
    }

    public static DepositRequestBody generate(Long accountId) {
        var request = generate();
        request.setId(accountId);
        return request;
    }

    public static DepositRequestBody generate(Long accountID, double balance) {
        return DepositRequestBody.builder()
                .id(accountID)
                .balance(balance)
                .build();
    }
}
