package ru.kduskov.models.body.response;

import lombok.*;
import ru.kduskov.models.body.response.Transaction;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Account {
    private Long id;
    private String accountNumber;
    private Double balance;
    private List<Transaction> transactions;
}
