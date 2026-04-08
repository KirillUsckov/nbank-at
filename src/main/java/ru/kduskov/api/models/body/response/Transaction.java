package ru.kduskov.api.models.body.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kduskov.api.deserializers.CustomLocalDateTimeDeserializer;
import ru.kduskov.api.enums.Status;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private Double amount;
    private TransactionType type;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
    private Status status;
    private Long relatedAccountId;
    private boolean fraudCheckRequired;
}