package ru.kduskov.api.models.body.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import ru.kduskov.api.annotations.GeneratingRule;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChangeUserProfileRequestBody extends BaseRequest {
    @GeneratingRule(regex = "^[A-Za-z]{0,50} [A-Za-z]{0,50}$")
    private String name;

    @Override
    public String toString() {
        return "ChangeUserProfileRequestBody{name = " + name + "}";
    }
}
