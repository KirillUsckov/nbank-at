package ru.kduskov.api.requests.skelethon.requesters;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.request.BaseRequest;
import ru.kduskov.api.requests.skelethon.interfaces.CrudEndpointInterface;
import ru.kduskov.api.requests.skelethon.HttpRequest;

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
    public ValidatableResponse post() {
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
    public ValidatableResponse get(String urlParam) {
        var url = endpoint.getUrlWithParam(urlParam);
        return given()
                .spec(requestSpecification)
                .get(url)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete(long id) {
        return given()
                .spec(requestSpecification)
                .delete(endpoint.getEndpoint() + id)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
