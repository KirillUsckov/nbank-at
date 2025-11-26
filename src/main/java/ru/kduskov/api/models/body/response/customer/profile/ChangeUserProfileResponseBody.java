package ru.kduskov.api.models.body.response.customer.profile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;

@Data
@SuperBuilder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
public class ChangeUserProfileResponseBody extends BaseResponse {
    private UserProfileResponseBody customer;
}
