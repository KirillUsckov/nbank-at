package ru.kduskov.api.models.body.response.accounts.transfer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.models.body.response.BaseResponse;

import java.math.BigDecimal;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransferResponseBody extends BaseResponse {
    private Long senderAccountId;
    private Long receiverAccountId;
    private BigDecimal amount;
}
