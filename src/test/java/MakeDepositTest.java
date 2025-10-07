import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.enums.TransactionType;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.MakeDepositRequestBody;
import ru.kduskov.models.body.response.Account;
import ru.kduskov.models.body.response.Transaction;
import ru.kduskov.requests.CreateAccountRequest;
import ru.kduskov.requests.MakeDepositRequest;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

import java.time.*;
import java.util.Comparator;

public class MakeDepositTest extends BaseTest {
    private static Account firstUserAccount;
    private static Account secondUserAccount;

    @BeforeAll
    public static void createAcc() {
        firstUserAccount = new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);

        var secondUserAuthToken = createRandomUser();
        secondUserAccount = new CreateAccountRequest(RequestSpecs.userSpec(secondUserAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);
    }

    @Test
    public void checkAdminCannotMakeDeposit() {
        new MakeDepositRequest(RequestSpecs.adminSpec(), ResponseSpecs.accessForbidden())
                .post(
                        MakeDepositRequestBody.builder()
                                .id(firstUserAccount.getId())
                                .build()
                );
    }

    @Test
    public void checkUserCanMakeDepositToHisOwnAccount() {
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        var depositResponseBody =
                new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .post(depositRequestBody).extract().as(Account.class);
        firstUserAccount.setBalance(firstUserAccount.getBalance() + depositRequestBody.getBalance());
        this.assertSingleTransaction(depositRequestBody, depositResponseBody, firstUserAccount);
    }

    @Test
    public void checkUserCantMakeDepositOnlyToOthersAccount() {
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(secondUserAccount.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden())
                .post(depositRequestBody);
    }

    @Test
    public void checkUserCantMakeDepositOnlyToNotExistedAccount() {
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(RandomData.getId())
                        .balance(RandomData.getValidDepositAmount())
                        .build();
        new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden())
                .post(depositRequestBody);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4999, 5000})
    public void checkUserCanMakeDepositOnlyWithBalanceInRange(int balance) {
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(balance)
                        .build();
        var depositResponseBody =
                new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .post(depositRequestBody).extract().as(Account.class);
        var totalBalance = firstUserAccount.getBalance() + balance;
        firstUserAccount.setBalance(totalBalance);
        this.assertSingleTransaction(depositRequestBody, depositResponseBody, firstUserAccount);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 5001})
    public void checkUserCanNotMakeDepositOnlyWithBalanceOutOfRange(int balance) {
        var depositRequestBody =
                MakeDepositRequestBody.builder()
                        .id(firstUserAccount.getId())
                        .balance(balance)
                        .build();
        new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .post(depositRequestBody);
    }

    private void assertSingleTransaction(MakeDepositRequestBody depositRequestBody, Account depositResponseBody, Account userAccount) {
        softly.assertThat(depositResponseBody.getId())
                .withFailMessage("id from response != id from request")
                .isEqualTo(depositRequestBody.getId());
        softly.assertThat(depositResponseBody.getBalance())
                .withFailMessage("balance from response != balance from account")
                .isEqualTo(userAccount.getBalance());

        softly.assertThat(depositResponseBody.getAccountNumber()).isEqualTo(userAccount.getAccountNumber());

        softly.assertThat(depositResponseBody.getTransactions().size()).withFailMessage("List of transactions has == 0 value").isGreaterThan(0);
        var transaction = depositResponseBody.getTransactions().stream().max(Comparator.comparing(Transaction::getId))
                .orElseThrow();
        softly.assertThat(transaction.getId())
                .withFailMessage("Transaction id == 0")
                .isGreaterThan(0);

        softly.assertThat(transaction.getAmount())
                .withFailMessage("Transaction amount does not match the value from the request")
                .isEqualTo(depositRequestBody.getBalance());

        softly.assertThat(transaction.getType())
                .withFailMessage("Transaction type != DEPOSIT")
                .isEqualTo(TransactionType.DEPOSIT);

        softly.assertThat(transaction.getRelatedAccountId())
                .withFailMessage("Related account id does not match the value from the request")
                .isEqualTo(depositRequestBody.getId());

        var moscowTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        var serverTime = transaction.getTimestamp();

        var serverMoscowTime = serverTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        long secondsDiff = Math.abs(Duration.between(serverMoscowTime, moscowTime).getSeconds());

        softly.assertThat(secondsDiff)
                .withFailMessage("Transaction time difference is more than 30 seconds. Difference: " + secondsDiff + "s")
                .isLessThanOrEqualTo(30);

    }
}
