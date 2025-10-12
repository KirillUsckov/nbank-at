package steps;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.enums.Role;
import ru.kduskov.enums.TransactionType;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.CreateUserRequestBody;
import ru.kduskov.models.body.request.MakeDepositRequestBody;
import ru.kduskov.models.body.request.TransferMoneyRequestBody;
import ru.kduskov.models.body.response.Account;
import ru.kduskov.models.body.response.Customer;
import ru.kduskov.models.body.response.Transaction;
import ru.kduskov.models.body.response.accounts.transfer.TransferMoneyResponseBody;
import ru.kduskov.requests.*;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSteps {
    private static final int MAX_DEPOSIT_PER_DEPOSIT_TRANSACTION = 5_000;
    private SoftAssertions softly;

    public UserSteps(SoftAssertions softly) {
        this.softly = softly;
    }

    public static String createRandomUser() {
        return new CreateUserRequest(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
                .post(
                        CreateUserRequestBody.builder()
                                .username(RandomData.getUsername())
                                .password(RandomData.getPassword())
                                .role(Role.USER).build()
                ).extract().header("Authorization");
    }

    public static Account createAccount(String userAuthToken) {
        return new CreateAccountRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.entityWasCreated())
                .post().extract().as(Account.class);
    }

    public static void makeDeposit(Account account, String userAuthToken, int deposit) {
        int remainingAmount = deposit;
        while (remainingAmount > 0) {
            var currentDeposit = Math.min(remainingAmount, MAX_DEPOSIT_PER_DEPOSIT_TRANSACTION);
            new MakeDepositRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                    .post(MakeDepositRequestBody.builder()
                            .id(account.getId())
                            .balance(currentDeposit)
                            .build());
            remainingAmount -= currentDeposit;
        }
    }

    public Customer getCustomer(String userAuthToken) {
        return new GetUserProfileRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                .get()
                .extract()
                .as(Customer.class);
    }

    public List<Account> getUserAccounts(String userAuthToken) {
        return this.getCustomer(userAuthToken).getAccounts();
    }

    public void assertTransferAmountLessThanMaximum(TransferMoneyRequestBody body, String userAuthToken) {
        var message = new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .post(body).extract().body().asString();
        assertEquals("Transfer amount cannot exceed 10000", message);
    }

    public void assertAmountIsMoreThanMinimum(TransferMoneyRequestBody body, String userAuthToken) {

        var message = new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .post(body).extract().body().asString();
        assertEquals("Transfer amount must be at least 0.01", message);
    }

    public void assertInvalidTransfer(TransferMoneyRequestBody transferRequestBody, String userAuthToken) {
        var message = new TransferMoneyRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .post(transferRequestBody).extract().body().asString();
        assertEquals("Invalid transfer: insufficient funds or invalid accounts", message);
    }

    public void assertTransferResponse(TransferMoneyRequestBody transferRequestBody, TransferMoneyResponseBody depositResponseBody) {
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

    public void assertBalanceWasNotChanged(List<Account> accountsBeforeRequest, List<Account> accountsAfterRequest, Account account) {
        var accountBeforeRequest = accountsBeforeRequest.stream().filter(a -> a.getAccountNumber().equals(account.getAccountNumber())).findFirst();
        var accountAfterRequest = accountsAfterRequest.stream().filter(a -> a.getAccountNumber().equals(account.getAccountNumber())).findFirst();
        softly.assertThat(accountBeforeRequest.isPresent()).withFailMessage("Account before request has no value").isTrue();
        softly.assertThat(accountAfterRequest.isPresent()).withFailMessage("Account after request has no value").isTrue();
        softly.assertThat(accountAfterRequest.get()).withFailMessage("Account was changed").isEqualTo(accountBeforeRequest.get());
    }

    public void assertBalanceWasIncreased(List<Account> accountsBeforeRequest, List<Account> accountsAfterRequest, Account account, long depositAmount) {
        var accountBeforeRequestOpt = accountsBeforeRequest.stream().filter(a -> a.getAccountNumber().equals(account.getAccountNumber())).findFirst();
        var accountAfterRequestOpt = accountsAfterRequest.stream().filter(a -> a.getAccountNumber().equals(account.getAccountNumber())).findFirst();
        softly.assertThat(accountBeforeRequestOpt.isPresent()).withFailMessage("Account before request has no value").isTrue();
        softly.assertThat(accountAfterRequestOpt.isPresent()).withFailMessage("Account after request has no value").isTrue();
        var accountBeforeRequest = accountBeforeRequestOpt.get();
        var accountAfterRequest = accountAfterRequestOpt.get();
        softly.assertThat(accountAfterRequest.getBalance()).withFailMessage("Account balance after request <= balance before request").isEqualTo(accountBeforeRequest.getBalance() + depositAmount);
    }

    public void assertSingleDeposit(MakeDepositRequestBody depositRequestBody, Account depositResponseBody, Account userAccount) {
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

        var secondsDiff = Math.abs(Duration.between(serverMoscowTime, moscowTime).getSeconds());

        softly.assertThat(secondsDiff)
                .withFailMessage("Transaction time difference is more than 30 seconds. Difference: " + secondsDiff + "s")
                .isLessThanOrEqualTo(30);

    }

    public void assertBalanceForReceiverAndSenderAfterTransferWasChanged(Account senderAccountBeforeRequest, Account receiverAccountBeforeRequest, Account senderAccountAfterRequest, Account receiverAccountAfterRequest, int amount) {
        var actualSenderBalance = senderAccountAfterRequest.getBalance();
        var expectedSenderBalance = senderAccountBeforeRequest.getBalance() - amount;
        softly.assertThat(actualSenderBalance)
                .withFailMessage(String.format("Senders account balance is not equals expected:\nExpected:%s\nActual:%s", expectedSenderBalance, actualSenderBalance))
                .isEqualTo(expectedSenderBalance);

        var actualReceiverBalance = receiverAccountAfterRequest.getBalance();
        var expectedReceiverBalance = receiverAccountBeforeRequest.getBalance() + amount;
        softly.assertThat(actualReceiverBalance)
                .withFailMessage(String.format("Receiver account balance is not equals expected:\nExpected:%s\nActual:%s", expectedReceiverBalance, actualReceiverBalance))
                .isEqualTo(expectedReceiverBalance);
    }

    public void assertBalanceForReceiverAndSenderAfterTransferWasNotChanged(Account senderAccountBeforeRequest, Account receiverAccountBeforeRequest, Account senderAccountAfterRequest, Account receiverAccountAfterRequest) {
        softly.assertThat(senderAccountAfterRequest.getBalance())
                .withFailMessage("Senders account balance was changed")
                .isEqualTo(senderAccountBeforeRequest.getBalance());
        softly.assertThat(receiverAccountAfterRequest.getBalance())
                .withFailMessage("Receiver account balance was changed")
                .isEqualTo(receiverAccountBeforeRequest.getBalance());
    }
}
