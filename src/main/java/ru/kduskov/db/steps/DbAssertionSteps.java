package ru.kduskov.db.steps;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.assertions.Assertions;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.api.steps.assertions.BaseAssertionsSteps;
import ru.kduskov.db.assertions.DbAssertions;
import ru.kduskov.db.models.dao.AccountDao;
import ru.kduskov.db.models.dao.CustomerDao;

import java.util.List;

public class DbAssertionSteps {
    protected final DbAssertions assertions;

    public DbAssertionSteps(SoftAssertions softly) {
        assertions = new DbAssertions(softly);
    }

    public void assertCustomerDaoMatchUserProfileResponse(CustomerDao customerDao, UserProfileResponseBody userProfileResponseBody) {
        assertions.assertThat(customerDao).matches(userProfileResponseBody);
    }

    public void assertCustomerDaoMatchChangeUserProfileResponse(CustomerDao customerDao, ChangeUserProfileResponseBody changeUserProfileResponseBody) {
        assertions.assertThat(customerDao).matches(changeUserProfileResponseBody);
    }

    public void assertAccountDaoListEquals(List<AccountDao> actual, List<AccountDao> expected) {
        assertions.assertThat(actual).isEqualTo(expected);
    }
}
