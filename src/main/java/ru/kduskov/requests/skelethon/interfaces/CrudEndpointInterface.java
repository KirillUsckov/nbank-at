package ru.kduskov.requests.skelethon;

import ru.kduskov.models.body.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object get(long id);
    Object update(long id, BaseModel model);
    Object delete(long id);

}
