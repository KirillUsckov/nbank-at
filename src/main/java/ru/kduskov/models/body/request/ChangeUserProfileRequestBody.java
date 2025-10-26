package ru.kduskov.models.body.request;

import lombok.*;
import ru.kduskov.annotations.GeneratingRule;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChangeUserProfileRequestBody extends BaseRequest {
    @GeneratingRule(regex = "^[A-Za-z]+\\s{1}[A-Za-z]+$")
    private String name;
}
