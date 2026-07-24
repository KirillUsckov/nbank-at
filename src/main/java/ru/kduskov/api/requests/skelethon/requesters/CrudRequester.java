package ru.kduskov.api.requests.skelethon.requesters;

import io.qameta.allure.Step;
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
        return post(endpoint, body);
    }

    @Override
    public ValidatableResponse post() {
        return post(endpoint, null);
    }

    @Override
    public ValidatableResponse put(BaseRequest body) {
        return put(endpoint, body);
    }

    @Override
    public ValidatableResponse get() {
        return get(endpoint);
    }

    @Override
    public ValidatableResponse get(String urlParam) {
        return get(endpoint, urlParam);
    }

    @Override
    public ValidatableResponse delete(long id) {
        return delete(endpoint, id);
    }

    @Step("POST {endpoint} with body {body}")
    private ValidatableResponse post(Endpoint endpoint, BaseRequest body) {
        return given()
                .spec(requestSpecification)
                .body(body == null ? "" : body)
                .post(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Step("PUT {endpoint} with body {body}")
    private ValidatableResponse put(Endpoint endpoint, BaseRequest body) {
        return given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(body == null ? "" : body)
                .put(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Step("GET {endpoint}")
    private ValidatableResponse get(Endpoint endpoint) {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Step("GET {endpoint} with query params {urlParam}")
    private ValidatableResponse get(Endpoint endpoint, String urlParam) {
        var url = endpoint.getUrlWithParam(urlParam);
        return given()
                .spec(requestSpecification)
                .get(url)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Step("DELETE {endpoint} for id {id}")
    private ValidatableResponse delete(Endpoint endpoint, long id) {
        return given()
                .spec(requestSpecification)
                .delete(endpoint.getEndpoint() + id)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
