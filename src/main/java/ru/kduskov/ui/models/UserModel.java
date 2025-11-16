package ru.kduskov.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private String username;
    private String password;
    private String token;

    public static UserModel fromCreateUserRequest(CreateUserRequestBody createUserRequestBody, String authToken) {
        return new UserModel(createUserRequestBody.getUsername(), createUserRequestBody.getPassword(), authToken);
    }
}
