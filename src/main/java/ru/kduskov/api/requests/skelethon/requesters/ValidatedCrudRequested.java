package ru.kduskov.api.requests.skelethon.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.models.body.request.BaseRequest;
import ru.kduskov.api.requests.skelethon.HttpRequest;
import ru.kduskov.api.requests.skelethon.interfaces.CrudEndpointInterface;

public class ValidatedCrudRequested<M extends BaseResponse> extends HttpRequest implements CrudEndpointInterface {
    private CrudRequester crudRequester;

    public ValidatedCrudRequested(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
        crudRequester = new CrudRequester(requestSpecification, responseSpecification, endpoint);
    }

    @Override
    public M post(BaseRequest model) {
        return (M) crudRequester.post(model).extract().as(endpoint.getResponseClass());
    }

    @Override
    public M post() {
        return  (M) crudRequester.post(null).extract().as(endpoint.getResponseClass());
    }

    @Override
    public M put(BaseRequest model) {
        return (M) crudRequester.put(model).extract().as(endpoint.getResponseClass());
    }

    @Override
    public M get() {
        return (M) crudRequester.get().extract().as(endpoint.getResponseClass());
    }

    @Override
    public M get(String urlParam) {
        return (M) crudRequester.get(urlParam).extract().as(endpoint.getResponseClass());
    }

    @Override
    public M delete(long id) {
        return (M) crudRequester.delete(id).extract().as(endpoint.getResponseClass());
    }
}
