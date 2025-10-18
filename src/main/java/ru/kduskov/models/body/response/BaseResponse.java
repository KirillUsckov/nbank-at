package ru.kduskov.models.body.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseResponse {
    protected String message;
}
