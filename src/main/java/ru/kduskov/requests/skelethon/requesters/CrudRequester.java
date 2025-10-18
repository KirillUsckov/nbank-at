package ru.kduskov.requests.skelethon.requesters;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.models.body.request.BaseRequest;
import ru.kduskov.requests.skelethon.HttpRequest;
import ru.kduskov.requests.skelethon.interfaces.CrudEndpointInterface;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
    }

    @Override
    public ValidatableResponse post(BaseRequest body) {
        return given()
                .spec(requestSpecification)
                .body(body == null ? "" : body)
                .post(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Object post() {
        return post(null);
    }

    @Override
    public ValidatableResponse put(BaseRequest body) {
        return given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(body == null ? "" : body)
                .put(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Object update(long id, BaseRequest model) {
        return null;
    }

    @Override
    public Object delete(long id) {
        return null;
    }
}
