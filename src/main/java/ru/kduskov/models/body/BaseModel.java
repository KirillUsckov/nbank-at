package ru.kduskov.models.body;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseModel {
    protected String message;
}
