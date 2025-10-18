package ru.kduskov.models.body.request;

import lombok.*;
import ru.kduskov.annotations.GeneratingRule;
import ru.kduskov.enums.GenerationsRules;

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
