package ru.kduskov.api.models.body.request;

import lombok.*;
import ru.kduskov.api.annotations.GeneratingRule;
import ru.kduskov.api.enums.GenerationsRules;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DepositRequestBody extends BaseRequest {
    private Long id;
    @GeneratingRule(valueKey = GenerationsRules.DEPOSIT_BALANCE)
    private Double balance;
}
