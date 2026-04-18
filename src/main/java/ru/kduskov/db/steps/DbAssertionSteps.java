package ru.kduskov.db.steps;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.api.enums.TransactionType;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
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

    public void assertCustomerDaoMatchChangeUserProfileResponse(CustomerDao customerDao, ChangeUserProfileResponseBody changeUserProfileResponseBody) {
        assertions.assertThat(customerDao).matches(changeUserProfileResponseBody);
    }

    public void assertAccountDaoListEquals(List<AccountDao> actual, List<AccountDao> expected) {
        assertions.assertThat(actual).isEqualTo(expected);
    }

    public void assertAccountDaoEquals(AccountDao actual, AccountDao expected) {
        assertions.assertThat(actual).isEqualTo(expected, true);
    }

    public void assertAccountDaoEqualsWithDiffDateUpdated(AccountDao actual, AccountDao expected) {
        assertions.assertThat(actual).isEqualTo(expected);
    }

    public void assertTransactionDaoEquals(TransactionDao actual, Long id, TransferResponseBody transferRes, TransactionType type) {
        var senderAccId = type == TransactionType.TRANSFER_IN ? transferRes.getReceiverAccountId() : transferRes.getSenderAccountId();
        var receiverAccId = type == TransactionType.TRANSFER_IN ? transferRes.getSenderAccountId() : transferRes.getReceiverAccountId();
        assertions.assertThat(actual).isEqualTo(id, senderAccId, receiverAccId, transferRes.getAmount(), type);
    }
}
