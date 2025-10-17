package ru.kduskov.requests;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class BaseRequest {
    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;
}
