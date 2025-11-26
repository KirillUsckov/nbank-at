package ru.kduskov.api.models.body.response.accounts;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.models.body.response.BaseResponse;

@Data
@SuperBuilder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
public class DeleteAccountResponse extends BaseResponse {
    private Long accountId;
}
