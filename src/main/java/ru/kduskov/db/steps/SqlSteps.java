package ru.kduskov.db.steps;

import ru.kduskov.db.DBRequest;
import ru.kduskov.db.enums.RequestType;
import ru.kduskov.db.enums.Tables;
import ru.kduskov.db.models.Condition;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.models.dao.CustomerDao;

import java.util.List;
import java.util.Optional;

public class SqlSteps {
    public static Optional<CustomerDao> findCustomerByUsername(String username) {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.CUSTOMERS)
                .where(Condition.equalTo("username", username))
                .extractAsSingle(CustomerDao.class);
    }

    public static Optional<AccountDao> findAccountByCustomerId(Long customerId) {
        return DBRequest.builder()
                .requestType(RequestType.SELECT)
                .table(Tables.ACCOUNTS)
                .where(Condition.equalTo("customer_id", customerId))
                .extractAsSingle(AccountDao.class);
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