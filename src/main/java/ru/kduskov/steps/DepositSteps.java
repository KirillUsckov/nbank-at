package ru.kduskov.steps;

import ru.kduskov.enums.Endpoint;
import ru.kduskov.models.body.request.DepositRequestBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
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
    public static void makeDepositWithAmountValidation(AccountResponseBody account, String userAuthToken, int deposit) {
        int remainingAmount = deposit;
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

    /**
     * Метод вызывает ручку для отправки депозита без проверки суммы
     *
     * @param account
     * @param userAuthToken
     * @param deposit
     */
    public void makeDeposit(AccountResponseBody account, String userAuthToken, int deposit) {
        new CrudRequester(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok(), Endpoint.MAKE_DEPOSIT)
                .post(DepositRequestBody.builder()
                        .id(account.getId())
                        .balance(deposit)
                        .build());

    }
}
