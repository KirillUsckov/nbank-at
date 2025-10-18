package ru.kduskov.requests.skelethon.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.models.body.response.BaseResponse;
import ru.kduskov.models.body.request.BaseRequest;
import ru.kduskov.requests.skelethon.HttpRequest;
import ru.kduskov.requests.skelethon.interfaces.CrudEndpointInterface;

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
    public Object update(long id, BaseRequest model) {
        return null;
    }

    @Override
    public Object delete(long id) {
        return null;
    }
}
