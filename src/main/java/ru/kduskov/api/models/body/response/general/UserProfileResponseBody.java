package ru.kduskov.api.models.body.response.general;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.enums.Role;
import ru.kduskov.db.models.dao.AccountDao;

import java.util.List;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserProfileResponseBody extends BaseResponse {
    private Long id;
    private String username;
    private String password;
    private List<AccountDao> accounts;
    private String name;
    private Role role;
}
