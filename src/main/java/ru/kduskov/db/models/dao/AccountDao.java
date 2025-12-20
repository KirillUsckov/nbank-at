package ru.kduskov.db.models.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDao {
    private Long id;
    private String accountNumber;
    private Double balance;
    private Long customerId;
}