package ru.kduskov.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kduskov.models.body.response.BaseResponse;
import ru.kduskov.models.body.request.*;
//import ru.kduskov.models.body.response.accounts.CreateAccountResponseBody;
//import ru.kduskov.models.body.response.accounts.deposit.MakeDepositResponseBody;
import ru.kduskov.models.body.response.accounts.transfer.TransferResponseBody;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.models.body.response.general.UserProfileResponseBody;
import ru.kduskov.models.body.response.customer.profile.ChangeUserProfileResponseBody;

@Getter
@AllArgsConstructor
public enum Endpoint {
    CHANGE_USER_PROFILE("/customer/profile", ChangeUserProfileRequestBody.class, ChangeUserProfileResponseBody.class),
    CREATE_ACCOUNT("/accounts", null, AccountResponseBody.class),
    CREATE_USER("/admin/users", CreateUserRequestBody.class, UserProfileResponseBody.class),
    GET_USER_PROFILE("/customer/profile", null, UserProfileResponseBody.class),
    MAKE_DEPOSIT("/accounts/deposit", DepositRequestBody.class, AccountResponseBody.class),
    TRANSFER("/accounts/transfer", TransferRequestBody.class, TransferResponseBody.class);

    private final String endpoint;
    private final Class<? extends BaseRequest> requestClass;
    private final Class<? extends BaseResponse> responseClass;
}
