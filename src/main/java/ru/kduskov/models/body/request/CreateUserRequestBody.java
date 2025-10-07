package ru.kduskov.models.body.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kduskov.enums.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestBody {
    private String username;
    private String password;
    private Role role;
}
