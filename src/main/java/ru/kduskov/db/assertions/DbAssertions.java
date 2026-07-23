package ru.kduskov.db.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.assertions.list.AccountDaoListAssert;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.models.dao.CustomerDao;
import ru.kduskov.db.models.dao.TransactionDao;

import java.util.List;

public class DbAssertions {
    private final SoftAssertions softly;

    public DbAssertions(SoftAssertions softly) {
        this.softly = softly;
    }

    public CustomerDaoAssert assertThat(CustomerDao customerDao) {
        return CustomerDaoAssert.assertThat(customerDao, softly);
    }

    public AccountDaoListAssert assertThat(List<AccountDao> accountDaoList) {
        return AccountDaoListAssert.assertThat(accountDaoList, softly);
    }

    public AccountDaoAssert assertThat(AccountDao accountDao) {
        return AccountDaoAssert.assertThat(accountDao, softly);
    }

    public TransactionDaoAssert assertThat(TransactionDao transactionDao) {
        return TransactionDaoAssert.assertThat(transactionDao, softly);
    }
}