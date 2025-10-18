package ru.kduskov.models.body.response.general;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.kduskov.models.body.response.BaseResponse;
import ru.kduskov.models.body.response.Transaction;

import java.util.List;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AccountResponseBody extends BaseResponse {
    private Long id;
    private String accountNumber;
    private Double balance;
    private List<Transaction> transactions;
}
