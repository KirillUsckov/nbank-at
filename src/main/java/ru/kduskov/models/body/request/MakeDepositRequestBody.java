package ru.kduskov.models.body.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.kduskov.models.body.BaseModel;

@Data
@SuperBuilder
public class MakeDepositRequestBody extends BaseModel {
    private long id;
    private long balance;
}
