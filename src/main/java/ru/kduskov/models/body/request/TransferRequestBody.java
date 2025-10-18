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
    private Long senderAccountId;
    private Long receiverAccountId;
    @GeneratingRule(valueKey = GenerationsRules.TRANSFER_AMOUNT)
    private Double amount;
}
