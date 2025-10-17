package ru.kduskov.requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;

import static io.restassured.RestAssured.given;

public class ChangeUserProfileRequest extends BaseRequest {

    public ChangeUserProfileRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse put(ChangeUserProfileRequestBody body) {
        return given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(body)
                .put("/api/v1/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
