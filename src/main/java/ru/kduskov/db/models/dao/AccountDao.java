package ru.kduskov.db.models.dao;

import lombok.*;
import ru.kduskov.db.annotations.Column;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountDao extends BaseDao {
    @Column(name = "account_number")
    private String accountNumber;
    private BigDecimal balance;
    @Column(name = "customer_id")
    private Long customerId;
}
