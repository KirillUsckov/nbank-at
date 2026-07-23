package ru.kduskov.api.models.body.response.general;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.models.body.response.BaseResponse;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AccountResponseBody extends BaseResponse {
    private Long id;
    private String accountNumber;
    private Double balance;
    private Double depositAmount;
}
