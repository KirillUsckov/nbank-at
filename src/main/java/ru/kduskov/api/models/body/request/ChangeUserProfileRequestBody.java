package ru.kduskov.api.models.body.request;

import lombok.*;
import ru.kduskov.api.annotations.GeneratingRule;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChangeUserProfileRequestBody extends BaseRequest {
    @GeneratingRule(regex = "^[A-Za-z]{0,50} [A-Za-z]{0,50}$")
    private String name;
}
