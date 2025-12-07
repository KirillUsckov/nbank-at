package ru.kduskov.api.models.body.request;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginRequestBody extends BaseRequest {
    private String username;
    private String password;
}
