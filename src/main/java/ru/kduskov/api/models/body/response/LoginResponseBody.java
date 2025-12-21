package ru.kduskov.api.models.body.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.enums.Role;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LoginResponseBody extends BaseResponse {
    private String username;
    private Role role;
}
