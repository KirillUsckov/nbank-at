package ru.kduskov.steps;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.models.body.request.DepositRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

public class DepositSteps {
    private static final int MAX_DEPOSIT_PER_DEPOSIT_TRANSACTION = 5_000;

    /**
     * Метод вызывает ручку для отправки депозита, но из-за ограничения суммы в 5000,
     * если сумма депозита больше, то она разбивается на несколько итераций
     *
     * @param account
     * @param userAuthToken
     * @param deposit
     */
    public static void sendDepositWithAmountValidation(AccountResponseBody account, String userAuthToken, double deposit) {
        double remainingAmount = deposit;
        while (remainingAmount > 0) {
            var currentDeposit = Math.min(remainingAmount, MAX_DEPOSIT_PER_DEPOSIT_TRANSACTION);
            new CrudRequester(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok(), Endpoint.MAKE_DEPOSIT)
                    .post(DepositRequestBody.builder()
                            .id(account.getId())
                            .balance(currentDeposit)
                            .build());
            remainingAmount -= currentDeposit;
        }
    }

    public AccountResponseBody sendDeposit(DepositRequestBody requestBody, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        return new ValidatedCrudRequested<AccountResponseBody>(requestSpecification, responseSpecification, Endpoint.MAKE_DEPOSIT)
                .post(requestBody);

    }

    public String sendDepositWithStringResponse(DepositRequestBody body, String userAuthToken, ResponseSpecification responseSpecification) {
        return sendDepositWithStringResponse(body, RequestSpecs.userSpec(userAuthToken), responseSpecification);
    }

    public String sendDepositWithStringResponse(DepositRequestBody body, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        return new CrudRequester(requestSpecification, responseSpecification, Endpoint.MAKE_DEPOSIT)
                .post(body)
                .extract()
                .body()
                .asString();
    }

}
