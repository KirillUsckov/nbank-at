package ru.kduskov.api.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.kduskov.api.models.body.response.customer.profile.CustomerAccountsResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.util.List;

public class CustomCustomerAccountsResponseDeserializer extends JsonDeserializer<CustomerAccountsResponseBody> {
    @Override
    public CustomerAccountsResponseBody deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
        return new CustomerAccountsResponseBody(
                p.readValueAs(
                        new TypeReference<List<AccountResponseBody>>() {}
                )
        );
    }
}