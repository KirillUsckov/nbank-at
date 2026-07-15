package ru.kduskov.api.requests.skelethon.interfaces;

import ru.kduskov.api.models.body.request.BaseRequest;

public interface CrudEndpointInterface {
    Object post(BaseRequest model);
    Object post();
    Object put(BaseRequest model);
    Object get();
    Object get(String urlParam);
    Object delete(long id);

}
