package ru.kduskov.db.steps;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.db.assertions.DbAssertions;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.models.dao.CustomerDao;
import ru.kduskov.db.models.dao.TransactionDao;

import java.math.BigDecimal;
import java.util.List;

public class DbAssertionSteps {
    protected final DbAssertions assertions;

    public DbAssertionSteps(SoftAssertions softly) {
        assertions = new DbAssertions(softly);
    }

    public void assertCustomerDaoMatchUserProfileResponse(CustomerDao customerDao, UserProfileResponseBody userProfileResponseBody) {
        assertions.assertThat(customerDao).matches(userProfileResponseBody);
    }

    public void assertCustomerDaoMatchCreateUserRequest(CustomerDao customerDao, CreateUserRequestBody createUserRequestBody) {
        assertions.assertThat(customerDao).matches(createUserRequestBody);
    }

    public void assertCustomerDaoMatchChangeUserProfileResponse(
            CustomerDao customerDao,
            ChangeUserProfileResponseBody changeUserProfileResponseBody
    ) {
        assertions.assertThat(customerDao).matches(changeUserProfileResponseBody);
    }

    public void assertAccountDaoListEquals(List<AccountDao> actual, List<AccountDao> expected) {
        assertions.assertThat(actual).isEqualTo(expected);
    }

    public void assertAccountDaoEquals(AccountDao actual, AccountDao expected, boolean ignoreDateUpdated) {
        assertions.assertThat(actual).matches(expected, ignoreDateUpdated);
    }

    public void assertTransactionDaoEquals(
            TransactionDao actual,
            Long id,
            Long senderAccountId,
            Long receiverAccountId,
            BigDecimal amount,
            TransactionType type
    ) {
        assertions.assertThat(actual).isEqualTo(id, senderAccountId, receiverAccountId, amount, type);
    }
}