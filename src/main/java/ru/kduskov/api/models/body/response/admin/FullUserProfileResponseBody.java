package ru.kduskov.api.models.body.response.admin;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.api.enums.Role;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.util.List;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FullUserProfileResponseBody extends BaseResponse {
    List<FullUserProfileInfo> data;
}
