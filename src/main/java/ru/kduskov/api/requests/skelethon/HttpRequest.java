package ru.kduskov.api.requests.skelethon;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.AllArgsConstructor;
import ru.kduskov.api.enums.Endpoint;

@AllArgsConstructor
public class HttpRequest {
    protected final RequestSpecification requestSpecification;
    protected final ResponseSpecification responseSpecification;
    protected final Endpoint endpoint;
}
