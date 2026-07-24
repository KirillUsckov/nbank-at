package ru.kduskov.api.models.body.response.accounts;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.kduskov.api.deserializers.CustomTransactionsResponseDeserializer;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.models.body.response.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(using = CustomTransactionsResponseDeserializer.class)
public class TransactionsResponseBody extends BaseResponse {
    private List<Transaction> transactions;
}
