package ru.kduskov.models.body.response.accounts.transfer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.models.body.response.BaseResponse;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransferResponseBody extends BaseResponse {
    private Long senderAccountId;
    private Long receiverAccountId;
    private Double amount;
}
