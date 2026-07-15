package ru.kduskov.db.models.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.db.annotations.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransactionDao  extends BaseDao {
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionType type;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "related_account_id")
    private Long relatedAccountId;
}
