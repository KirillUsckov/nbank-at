package ru.kduskov.models.body.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.kduskov.models.body.BaseModel;

@Data
@SuperBuilder
public class TransferMoneyRequestBody extends BaseModel {
    private long senderAccountId;
    private long receiverAccountId;
    private double amount;
}
