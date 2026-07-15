package ru.kduskov.api.steps;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.request.DepositRequestBody;
import ru.kduskov.api.models.body.response.accounts.deposit.DepositResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.requests.skelethon.requesters.ValidatedCrudRequested;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;

public final class DepositSteps {
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
                            .accountId(account.getId())
                            .amount(currentDeposit)
                            .build());
            remainingAmount -= currentDeposit;
        }
    }

    public DepositResponseBody sendDeposit(DepositRequestBody requestBody, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        return new ValidatedCrudRequested<DepositResponseBody>(requestSpecification, responseSpecification, Endpoint.MAKE_DEPOSIT)
                .post(requestBody);

    }

    public String sendDepositWithStringResponse(DepositRequestBody body, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        return new CrudRequester(requestSpecification, responseSpecification, Endpoint.MAKE_DEPOSIT)
                .post(body)
                .extract()
                .body()
                .asString();
    }

}
