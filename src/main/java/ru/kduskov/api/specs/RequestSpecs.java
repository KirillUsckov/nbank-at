package ru.kduskov.api.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.kduskov.common.confs.Config;
import ru.kduskov.ui.utils.TestDataReader;

import javax.swing.plaf.PanelUI;
import java.util.List;

import static ru.kduskov.common.enums.ConfigParams.API_VERSION;
import static ru.kduskov.common.enums.ConfigParams.SERVER;

public final class RequestSpecs {
    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty(SERVER.getValue()) + Config.getProperty(API_VERSION.getValue()));
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
