package ru.kduskov.models.body.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.kduskov.annotations.GeneratingRule;
import ru.kduskov.enums.GenerationsRules;
import ru.kduskov.models.body.BaseModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferMoneyRequestBody extends BaseRequest {
    private long senderAccountId;
    private long receiverAccountId;
    @GeneratingRule(valueKey = GenerationsRules.TRANSFER_AMOUNT)
    private double amount;
}
