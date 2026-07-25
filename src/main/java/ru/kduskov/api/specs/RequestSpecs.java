package ru.kduskov.api.specs;

import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageV3RestAssured;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import ru.kduskov.api.constants.Headers;
import ru.kduskov.common.confs.Config;
import ru.kduskov.common.enums.ConfigParams;
import ru.kduskov.ui.utils.TestDataReader;

import java.util.List;

import static ru.kduskov.common.enums.ConfigParams.API_BASE_URL;

public final class RequestSpecs {

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(Config.getProperty(API_BASE_URL))
                .addFilters(List.of(new AllureRestAssured(), new SwaggerCoverageRestAssured(), new SwaggerCoverageV3RestAssured()));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader(Headers.AUTHORIZATION, TestDataReader.getAdmin().getToken())
                .build();
    }

    public static RequestSpecification userSpec(String token) {
        return defaultRequestBuilder()
                .addHeader(Headers.AUTHORIZATION, token)
                .build();
    }
}
