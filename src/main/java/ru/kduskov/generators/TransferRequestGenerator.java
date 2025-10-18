package ru.kduskov.generators;

import ru.kduskov.models.body.request.TransferRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;

public class TransferRequestGenerator {
    public static TransferRequestBody generate(AccountResponseBody sender, AccountResponseBody receiver, long amount) {
        return TransferRequestBody.builder()
                .senderAccountId(sender.getId())
                .receiverAccountId(receiver.getId())
                .amount(amount)
                .build();
    }
}
