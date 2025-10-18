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
public class MakeDepositRequestBody extends BaseRequest {
    private long id;
    @GeneratingRule(valueKey = GenerationsRules.DEPOSIT_BALANCE)
    private long balance;
}
