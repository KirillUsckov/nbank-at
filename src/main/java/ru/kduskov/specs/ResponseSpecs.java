package ru.kduskov.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

public final class ResponseSpecs {
    private static ResponseSpecBuilder defaultResponseSpec() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityWasCreated() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification accessForbidden() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .build();
    }

    public static ResponseSpecification ok() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification badRequest() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }
}
