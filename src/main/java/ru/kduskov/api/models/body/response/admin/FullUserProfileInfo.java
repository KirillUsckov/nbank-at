package ru.kduskov.api.models.body.response.admin;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kduskov.api.enums.Role;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullUserProfileInfo {
    private Long id;
    private String username;
    private String password;
    private String name;
    private Role role;
    @Nullable
    private List<AccountResponseBody> accounts;
}
