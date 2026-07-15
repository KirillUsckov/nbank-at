package ru.kduskov.api.models.body.response.customer.profile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.kduskov.api.deserializers.CustomCustomerAccountsResponseDeserializer;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@JsonDeserialize(using = CustomCustomerAccountsResponseDeserializer.class)
public class CustomerAccountsResponseBody extends BaseResponse {
    private List<AccountResponseBody> accounts;
}
