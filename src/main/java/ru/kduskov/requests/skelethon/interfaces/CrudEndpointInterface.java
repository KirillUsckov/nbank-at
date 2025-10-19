package ru.kduskov.requests.skelethon.interfaces;

import ru.kduskov.models.body.request.BaseRequest;

public interface CrudEndpointInterface {
    Object post(BaseRequest model);
    Object post();
    Object put(BaseRequest model);
    Object get();
    Object delete(long id);

}
