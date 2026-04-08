package ru.kduskov.api.models.body.response.accounts.deposit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.models.body.response.BaseResponse;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DepositResponseBody extends BaseResponse {
    private Long id;
    private String accountNumber;
    private Double balance;
    private Double depositAmount;
    private Long transactionId;
}
