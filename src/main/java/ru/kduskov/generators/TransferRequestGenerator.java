package ru.kduskov.generators;

import ru.kduskov.generators.common.RequestDataGenerator;
import ru.kduskov.models.body.request.TransferRequestBody;

public class TransferRequestGenerator {
    public static TransferRequestBody generateWithSender() {
        return RequestDataGenerator.generateFilledObject(TransferRequestBody.class);
    }

    public static TransferRequestBody generateWithSender(Long senderId, Long receiverId, double amount) {
        return TransferRequestBody.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();
    }

    public static TransferRequestBody generateWithSender(Long senderId) {
        var request = generateWithSender();
        request.setSenderAccountId(senderId);
        return request;
    }


    public static TransferRequestBody generateWithReceiver(Long receiverId) {
        var request = generateWithSender();
        request.setReceiverAccountId(receiverId);
        return request;
    }

    public static TransferRequestBody generateWithSender(Long senderId, Long receiverId) {
        var request = generateWithSender(senderId);
        request.setReceiverAccountId(receiverId);
        return request;
    }
}
