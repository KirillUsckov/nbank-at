package ru.kduskov.models.body.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kduskov.models.body.response.Transaction;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private String accountNumber;
    private Double balance;
    private List<Transaction> transactions;
}
