package support;

import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.steps.SqlSteps;

import java.math.BigDecimal;

public final class ExpectedAccountState {
    private ExpectedAccountState() {
    }

    public static AccountDao unchanged(AccountResponseBody account) {
        return SqlSteps.getAccountByAccountNumber(account.getAccountNumber());
    }

    public static AccountDao withBalance(AccountResponseBody account, double balance) {
        var accountDao = unchanged(account);
        accountDao.setBalance(BigDecimal.valueOf(balance));
        return accountDao;
    }

    public static AccountDao increasedBy(AccountResponseBody account, double amount) {
        var accountDao = unchanged(account);
        accountDao.setBalance(accountDao.getBalance().add(BigDecimal.valueOf(amount)));
        return accountDao;
    }

    public static AccountDao decreasedBy(AccountResponseBody account, double amount) {
        var accountDao = unchanged(account);
        accountDao.setBalance(accountDao.getBalance().subtract(BigDecimal.valueOf(amount)));
        return accountDao;
    }
}
