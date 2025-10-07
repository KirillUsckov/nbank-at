package ru.kduskov.models.body.response.accounts.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.models.body.BaseModel;

@Data
@Jacksonized
@SuperBuilder
public class TransferMoneyResponseBody extends BaseModel {
    private Long senderAccountId;
    private Long receiverAccountId;
    private Double amount;
}
