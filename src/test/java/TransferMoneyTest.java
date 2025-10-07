import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.kduskov.enums.TransactionType;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.MakeDepositRequestBody;
import ru.kduskov.models.body.request.TransferMoneyRequestBody;
import ru.kduskov.models.body.response.Account;
import ru.kduskov.models.body.response.accounts.transfer.TransferMoneyResponseBody;
import ru.kduskov.requests.CreateAccountRequest;
import ru.kduskov.requests.MakeDepositRequest;
import ru.kduskov.requests.TransferMoneyRequest;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferMoneyTest extends BaseTest {
    private static Account firstUserFirstAccount;
    private static Account firstUserSecondAccount;
    private static Account secondUserAccount;
    private static String secondUserAuthToken;

    @BeforeAll
    public static void createAcc() {
        firstUserFirstAccount = new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);
        for (int i = 0; i < 10; i++)
            new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                    .post(MakeDepositRequestBody.builder()
                            .id(firstUserFirstAccount.getId())
                            .balance(5_000)
                            .build());
        firstUserSecondAccount = new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);

        secondUserAuthToken = createRandomUser();
        secondUserAccount = new CreateAccountRequest(RequestSpecs.userSpec(secondUserAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCanMakeTransferToHisAnotherAccountWithValidAmountIfMoneyEnough(int amount) {
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(amount)
                        .build();
        var depositResponseBody =
                new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .post(transferRequestBody).extract().as(TransferMoneyResponseBody.class);
        this.assertResponse(transferRequestBody, depositResponseBody);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCanMakeTransferToAnotherUserAccountWithValidAmountIfMoneyEnough(int amount) {
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(secondUserAccount.getId())
                        .amount(amount)
                        .build();
        var depositResponseBody =
                new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .post(transferRequestBody).extract().as(TransferMoneyResponseBody.class);
        this.assertResponse(transferRequestBody, depositResponseBody);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCantMakeTransferToAnotherUserAccountWithValidAmountIfMoneyNotEnough(int amount) {
        var accountWithNoMoney = new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(accountWithNoMoney.getId())
                        .receiverAccountId(secondUserAccount.getId())
                        .amount(amount)
                        .build();
        this.assertInvalidTransfer(transferRequestBody);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 10_000})
    public void checkUserCantMakeTransferToOwnUserAccountWithValidAmountIfMoneyNotEnough(int amount) {
        var accountWithNoMoney = new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(accountWithNoMoney.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(amount)
                        .build();
        this.assertInvalidTransfer(transferRequestBody);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void checkUserCantMakeTransferWithTooLowAmount(int amount) {
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(amount)
                        .build();

        var message = new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .post(transferRequestBody).extract().body().asString();
        assertEquals("Transfer amount must be at least 0.01", message);
    }

    @Test
    public void checkUserCantMakeTransferWithTooBogAmount() {
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(1_0001)
                        .build();

        var message = new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .post(transferRequestBody).extract().body().asString();
        assertEquals("Transfer amount cannot exceed 10000", message);
    }

    @Test
    public void checkUserCantMakeTransferToNotExistedAccount() {
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(firstUserFirstAccount.getId())
                        .receiverAccountId(RandomData.getId())
                        .amount(RandomData.getValidTransferAmount())
                        .build();
        this.assertInvalidTransfer(transferRequestBody);
    }

    @Test
    public void checkUserCantMakeTransferFromNotExistedAccount() {
        var transferRequestBody =
                TransferMoneyRequestBody.builder()
                        .senderAccountId(RandomData.getId())
                        .receiverAccountId(firstUserSecondAccount.getId())
                        .amount(RandomData.getValidTransferAmount())
                        .build();

        new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.accessForbidden())
                .post(transferRequestBody);
    }

    private void assertInvalidTransfer(TransferMoneyRequestBody transferRequestBody) {
        var message = new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .post(transferRequestBody).extract().body().asString();
        assertEquals("Invalid transfer: insufficient funds or invalid accounts", message);

    }

    private void assertResponse(TransferMoneyRequestBody transferRequestBody, TransferMoneyResponseBody depositResponseBody) {
        softly.assertThat(depositResponseBody.getReceiverAccountId())
                .withFailMessage("ReceiverAccountId from response is not equal ReceiverAccountId from request")
                .isEqualTo(transferRequestBody.getReceiverAccountId());
        softly.assertThat(depositResponseBody.getSenderAccountId())
                .withFailMessage("SenderAccountId from response is not equal SenderAccountId from request")
                .isEqualTo(transferRequestBody.getSenderAccountId());
        softly.assertThat(depositResponseBody.getAmount())
                .withFailMessage("Amount from response is not equal Amount from request")
                .isEqualTo(transferRequestBody.getAmount());
        softly.assertThat(depositResponseBody.getMessage())
                .withFailMessage("Amount from response is not equal Amount from request")
                .isEqualTo("Transfer successful");
    }
}
