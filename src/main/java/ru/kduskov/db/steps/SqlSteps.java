package ru.kduskov.db.steps;

import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.common.assertions.OptionalAssert;
import ru.kduskov.db.DBRequest;
import ru.kduskov.db.enums.RequestType;
import ru.kduskov.db.enums.Tables;
import ru.kduskov.db.models.Condition;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.models.dao.CustomerDao;
import ru.kduskov.db.models.dao.TransactionDao;

import java.math.BigDecimal;
import java.util.*;

public class SqlSteps {
    public static void deleteAllUsers(List<String> usernames) {
        DBRequest.builder()
                .requestType(RequestType.DELETE)
                .table(Tables.CUSTOMERS)
                .where(Condition.in("username", new ArrayList<>(usernames)))
                .execute();
    }

    public static Optional<CustomerDao> findCustomerByUsername(String username) {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.CUSTOMERS)
                .where(Condition.equalTo("username", username))
                .extractAsSingle(CustomerDao.class);
    }

    public static Optional<TransactionDao> findAllTransactionsByAccountId(Long senderAccountId,
                                                                          Long receiverAccountId,
                                                                          double amount,
                                                                          TransactionType transactionType) {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.TRANSACTIONS)
                .where(List.of(Condition.equalTo("account_id", senderAccountId),
                        Condition.equalTo("related_account_id", receiverAccountId),
                        Condition.equalTo("type", transactionType.name()),
                        Condition.equalTo("amount", amount))
                )
                .extractAsSingle(TransactionDao.class);
    }

    public static Optional<TransactionDao> findTransactionById(Long id) {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.TRANSACTIONS)
                .where(List.of(
                        Condition.equalTo("id", id))
                )
                .extractAsSingle(TransactionDao.class);
    }

    public static Optional<AccountDao> findAccountByCustomerId(Long customerId) {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.ACCOUNTS)
                .where(Condition.equalTo("customer_id", customerId))
                .extractAsSingle(AccountDao.class);
    }

    public static AccountDao getAccountByAccountNumber(String accountNumber) {
        var optAccount = SqlSteps.findAccountByAccountNumber(accountNumber);
        OptionalAssert.assertThat(optAccount).isPresent();
        return optAccount.get();
    }

    public static Optional<AccountDao> findAccountByAccountNumber(String accountNumber) {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.ACCOUNTS)
                .where(Condition.equalTo("account_number", accountNumber))
                .extractAsSingle(AccountDao.class);
    }

    public static List<AccountDao> findAllAccountsByCustomerId(Long customerId) {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.ACCOUNTS)
                .where(Condition.equalTo("customer_id", customerId))
                .extractAs(AccountDao.class);
    }

    public static void setAccountBalance(String accountNumber, double balance) {
        DBRequest.builder()
                .requestType(RequestType.UPDATE)
                .table(Tables.ACCOUNTS)
                .where(Condition.equalTo("account_number", accountNumber))
                .set(Map.of("balance", balance))
                .execute();
    }

    public static List<AccountDao> findAllAccounts() {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.ACCOUNTS)
                .extractAs(AccountDao.class);
    }

    public static List<CustomerDao> selectAll() {
        // Предположим класс User с полями и аннотациями @Column где нужно
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.CUSTOMERS)
                .extractAs(CustomerDao.class);

    }
}