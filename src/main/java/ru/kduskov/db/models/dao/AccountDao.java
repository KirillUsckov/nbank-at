package ru.kduskov.db.models.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.kduskov.db.annotations.Column;

import java.math.BigDecimal;

import static ru.kduskov.common.utils.LogMaskingUtils.maskAccountNumber;

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

    @Override
    public String toString() {
        return "AccountDao{"
                + "accountNumber='" + maskAccountNumber(accountNumber) + '\''
                + ", balance=" + balance
                + ", customerId=" + customerId + '}';
    }
}
