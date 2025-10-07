package ru.kduskov.requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.models.body.request.MakeDepositRequestBody;

import static io.restassured.RestAssured.given;

public class MakeDepositRequest extends BaseRequest {

    public MakeDepositRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse post(MakeDepositRequestBody body) {
        return given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
