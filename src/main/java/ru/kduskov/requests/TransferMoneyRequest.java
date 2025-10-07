package ru.kduskov.requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.models.body.request.TransferMoneyRequestBody;

import static io.restassured.RestAssured.given;

public class TransferMoneyRequest extends BaseRequest {

    public TransferMoneyRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse post(TransferMoneyRequestBody body) {
        return given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
