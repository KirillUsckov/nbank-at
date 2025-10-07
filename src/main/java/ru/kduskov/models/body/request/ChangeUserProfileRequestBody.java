package ru.kduskov.models.body.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.kduskov.models.body.BaseModel;

@Data
@SuperBuilder
public class ChangeUserProfileRequestBody extends BaseModel {
    private String name;
}
