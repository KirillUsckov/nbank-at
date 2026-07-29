package ru.kduskov.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.models.body.request.DepositRequestBody;
import ru.kduskov.api.models.body.request.TransferRequestBody;
import ru.kduskov.api.models.body.request.LoginRequestBody;
import ru.kduskov.api.models.body.request.BaseRequest;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.models.body.response.LoginResponseBody;
import ru.kduskov.api.models.body.response.accounts.TransactionsResponseBody;
import ru.kduskov.api.models.body.response.accounts.DeleteAccountResponse;
import ru.kduskov.api.models.body.response.accounts.deposit.DepositResponseBody;
import ru.kduskov.api.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.api.models.body.response.admin.FullUserProfileResponseBody;
import ru.kduskov.api.models.body.response.customer.profile.CustomerAccountsResponseBody;
import ru.kduskov.api.models.body.response.general.AccountResponseBody;
import ru.kduskov.api.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.api.models.body.response.customer.profile.ChangeUserProfileResponseBody;

@Getter
@AllArgsConstructor
public enum Endpoint {
    CHANGE_USER_PROFILE("/customer/profile", null, ChangeUserProfileRequestBody.class, ChangeUserProfileResponseBody.class),
    CREATE_ACCOUNT("/accounts", null, null, AccountResponseBody.class),
    DELETE_ACCOUNT("/accounts/", null, null, DeleteAccountResponse.class),
    CREATE_USER("/admin/users", null, CreateUserRequestBody.class, UserProfileResponseBody.class),
    DELETE_USER("/admin/users/", null, null, null),
    GET_ALL_USERS("/admin/users", null, null, FullUserProfileResponseBody.class),
    GET_USER_PROFILE("/customer/profile", null, null, UserProfileResponseBody.class),
    MAKE_DEPOSIT("/accounts/deposit", null, DepositRequestBody.class, DepositResponseBody.class),
    TRANSFER("/accounts/transfer", null, TransferRequestBody.class, TransferResponseBody.class),
    TRANSFER_WITH_FRAUD("/accounts/transfer-with-fraud-check", null, TransferRequestBody.class, TransferResponseBody.class),
    AUTH_LOGIN("/auth/login", null, LoginRequestBody.class, LoginResponseBody.class),
    GET_CUSTOMER_ACCOUNTS("/customer/accounts", null, null, CustomerAccountsResponseBody.class),
    GET_ACCOUNT_TRANSACTIONS("/accounts/ACC_ID/transactions", "ACC_ID", null, TransactionsResponseBody.class);

    private final String endpoint;
    private final String replacement;
    private final Class<? extends BaseRequest> requestClass;
    private final Class<? extends BaseResponse> responseClass;

    public String getUrlWithParam(String param) {
        return this.endpoint.replace(this.replacement, param);
    }

    @Override
    public String toString() {
        return endpoint;
    }
}
