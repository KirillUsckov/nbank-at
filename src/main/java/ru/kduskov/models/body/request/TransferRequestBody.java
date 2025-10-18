package ru.kduskov.models.body.request;

import lombok.*;
import ru.kduskov.annotations.GeneratingRule;
import ru.kduskov.enums.GenerationsRules;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferRequestBody extends BaseRequest {
    private long senderAccountId;
    private long receiverAccountId;
    @GeneratingRule(valueKey = GenerationsRules.TRANSFER_AMOUNT)
    private long amount;
}
