package ru.kduskov.api.models.body.response.customer.profile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.models.body.response.accounts.Customer;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ChangeUserProfileResponseBody extends BaseResponse {
    private Customer customer;
}
