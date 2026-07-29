package ru.kduskov.api.models.body.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.annotations.GeneratingRule;
import ru.kduskov.api.constants.GenerationsRegexes;
import ru.kduskov.api.enums.Role;
import ru.kduskov.common.enums.GenerationsRules;

@Data
@Jacksonized
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateUserRequestBody extends BaseRequest {
    @GeneratingRule(regex = GenerationsRegexes.USERNAME)
    private String username;
    @GeneratingRule(regex = GenerationsRegexes.NAME)
    private String name;
    @GeneratingRule(valueKey = GenerationsRules.PASSWORD, minLength = 8, maxLength = 128)
    private String password;

    private Role role = Role.USER;

    public CreateUserRequestBody(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "CreateUserRequestBody{ username=" + username + ", passwordIsNotNull=" + !password.isEmpty() + ", role=" + role + "}";
    }
}
