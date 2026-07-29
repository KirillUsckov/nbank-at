package ru.kduskov.api.models.body.response.customer.profile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.enums.Role;
import ru.kduskov.api.models.body.response.BaseResponse;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ChangeUserProfileResponseBody extends BaseResponse {
    private Long id;
    private String username;
    private String name;
    private Role role;

    @Override
    public String toString() {
        return "ChangeUserProfileResponseBody{username=" + username + ", name=" + name + ", role=" + role + "}";
    }
}
