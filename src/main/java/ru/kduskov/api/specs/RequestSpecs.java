package ru.kduskov.api.specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.kduskov.common.confs.Config;
import ru.kduskov.ui.utils.TestDataReader;

import java.util.List;

import static ru.kduskov.common.enums.ConfigParams.API_VERSION;
import static ru.kduskov.common.enums.ConfigParams.API_BASE_URL;

public final class RequestSpecs {
    private static String userToken;

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(Config.getProperty(API_BASE_URL) + Config.getProperty(API_VERSION))
                .addFilters(List.of(new AllureRestAssured()));
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
