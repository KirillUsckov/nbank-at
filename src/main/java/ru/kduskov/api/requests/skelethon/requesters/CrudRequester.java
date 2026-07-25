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
import ru.kduskov.common.confs.Config;

import static io.restassured.RestAssured.given;
import static ru.kduskov.common.enums.ConfigParams.API_VERSION;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
    }

    protected String getEndpointWithApiVersion(Endpoint endpoint, String params) {
        return Config.getProperty(API_VERSION) + endpoint.getUrlWithParam(params);
    }

    protected String getEndpointWithApiVersion(Endpoint endpoint) {
        return  Config.getProperty(API_VERSION) + endpoint.getEndpoint();
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
    public ValidatableResponse delete(Long id) {
        return delete(endpoint, id);
    }

    @Step("POST {endpoint} with body {body}")
    private ValidatableResponse post(Endpoint endpoint, BaseRequest body) {
        return given()
                .spec(requestSpecification)
                .body(body == null ? "" : body)
                .post(getEndpointWithApiVersion(endpoint))
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
                .put(getEndpointWithApiVersion(endpoint))
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Step("GET {endpoint}")
    private ValidatableResponse get(Endpoint endpoint) {
        return given()
                .spec(requestSpecification)
                .get(getEndpointWithApiVersion(endpoint))
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Step("GET {endpoint} with query params {urlParam}")
    private ValidatableResponse get(Endpoint endpoint, String urlParam) {
        var url = getEndpointWithApiVersion(endpoint, urlParam);
        return given()
                .spec(requestSpecification)
                .get(url)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Step("DELETE {endpoint} for id {id}")
    private ValidatableResponse delete(Endpoint endpoint, Long id) {
        return given()
                .spec(requestSpecification)
                .delete(getEndpointWithApiVersion(endpoint) + id)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
