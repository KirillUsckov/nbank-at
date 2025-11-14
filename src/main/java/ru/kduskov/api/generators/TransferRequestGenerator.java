package ru.kduskov.api.generators;

import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.TransferRequestBody;

public class TransferRequestGenerator {
    public static TransferRequestBody generate() {
        return RequestDataGenerator.generateFilledObject(TransferRequestBody.class);
    }

    public static TransferRequestBody generate(Long senderId, Long receiverId, double amount) {
        return TransferRequestBody.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();
    }

    public static TransferRequestBody generateWithSender(Long senderId) {
        var request = generate();
        request.setSenderAccountId(senderId);
        return request;
    }


    public static TransferRequestBody generateWithReceiver(Long receiverId) {
        var request = generate();
        request.setReceiverAccountId(receiverId);
        return request;
    }

    public static TransferRequestBody generate(Long senderId, Long receiverId) {
        var request = generateWithSender(senderId);
        request.setReceiverAccountId(receiverId);
        return request;
    }
}
