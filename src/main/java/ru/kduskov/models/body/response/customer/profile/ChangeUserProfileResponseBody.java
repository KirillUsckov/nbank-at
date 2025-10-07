package ru.kduskov.models.body.response.customer.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.models.body.BaseModel;
import ru.kduskov.models.body.response.Customer;

@Data
@SuperBuilder
@Jacksonized
public class ChangeUserProfileResponseBody extends BaseModel {
    private Customer customer;
}
