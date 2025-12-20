package ru.kduskov.api.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.common.confs.Config;
import ru.kduskov.ui.utils.TestDataReader;

import javax.swing.plaf.PanelUI;
import java.util.List;

import static ru.kduskov.common.enums.ConfigParams.API_VERSION;
import static ru.kduskov.common.enums.ConfigParams.API_BASE_URL;

public final class RequestSpecs {
    private static String userToken;
    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(Config.getProperty(API_BASE_URL) + Config.getProperty(API_VERSION));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", TestDataReader.getAdmin().getToken())
                .build();
    }

    public static RequestSpecification userSpec(String token) {
        return defaultRequestBuilder()
                .addHeader("Authorization", token)
                .build();
    }
}
