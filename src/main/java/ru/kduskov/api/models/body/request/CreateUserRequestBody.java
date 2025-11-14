package ru.kduskov.api.models.body.request;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.annotations.GeneratingRule;
import ru.kduskov.api.enums.Role;

import static ru.kduskov.api.enums.GenerationsRules.PASSWORD;


@Data
@Jacksonized
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateUserRequestBody extends BaseRequest {
    @GeneratingRule(regex = "^[a-zA-Z0-9._-]{3,15}$")
    private String username;
    @GeneratingRule(valueKey = PASSWORD, minLength = 8, maxLength = 128)
    private String password;

    private Role role = Role.USER;
}
