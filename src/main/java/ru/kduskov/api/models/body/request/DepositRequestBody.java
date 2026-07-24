package ru.kduskov.api.models.body.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import ru.kduskov.api.annotations.GeneratingRule;
import ru.kduskov.api.enums.GenerationsRules;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DepositRequestBody extends BaseRequest {
    private Long accountId;
    @GeneratingRule(valueKey = GenerationsRules.DEPOSIT_BALANCE)
    private Double amount;

    @Override
    public String toString() {
        return "DepositRequestBody: accountId=" + accountId + ", amount=" + amount;
    }
}
