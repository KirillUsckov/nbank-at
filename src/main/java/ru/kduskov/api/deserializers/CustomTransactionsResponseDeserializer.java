package ru.kduskov.api.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.kduskov.api.models.body.response.Transaction;
import ru.kduskov.api.models.body.response.accounts.TransactionsResponseBody;

import java.util.List;

public class CustomTransactionsResponseDeserializer extends JsonDeserializer<TransactionsResponseBody> {
    @Override
    public TransactionsResponseBody deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
        return new TransactionsResponseBody(
                p.readValueAs(
                        new TypeReference<List<Transaction>>() {}
                )
        );
    }
}