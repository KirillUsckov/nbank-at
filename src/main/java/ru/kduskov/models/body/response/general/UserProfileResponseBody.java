package ru.kduskov.models.body.response.admin.users;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.models.body.BaseModel;
import ru.kduskov.models.body.response.Customer;

@Data
@Jacksonized
@SuperBuilder
public class CreateUserResponseBody extends BaseModel {
    private Customer body;
}
