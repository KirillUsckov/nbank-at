package ru.kduskov.models.body.response.admin.users;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.models.body.response.BaseResponse;
import ru.kduskov.models.body.response.general.UserProfileResponseBody;

import java.util.List;

@Data
@Jacksonized
@SuperBuilder
public class UsersListResponse extends BaseResponse {
    private List<UserProfileResponseBody> users;
}