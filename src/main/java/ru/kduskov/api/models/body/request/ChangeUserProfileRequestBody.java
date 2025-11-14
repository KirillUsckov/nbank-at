package ru.kduskov.api.models.body.request;

import lombok.*;
import ru.kduskov.api.annotations.GeneratingRule;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChangeUserProfileRequestBody extends BaseRequest {
    @GeneratingRule(regex = "^[A-Za-z]+ [A-Za-z]+$")
    private String name;
}
