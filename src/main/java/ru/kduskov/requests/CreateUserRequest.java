package ru.kduskov.requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.models.body.request.CreateUserRequestBody;

import static io.restassured.RestAssured.given;

public class CreateUserRequest extends BaseRequest{
    public CreateUserRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse post(CreateUserRequestBody body) {
        return given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/v1/admin/users")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
