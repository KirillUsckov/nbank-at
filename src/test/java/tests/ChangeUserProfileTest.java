package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.response.Customer;
import ru.kduskov.models.body.response.customer.profile.ChangeUserProfileResponseBody;
import ru.kduskov.requests.ChangeUserProfileRequest;
import ru.kduskov.requests.GetUserProfileRequest;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeUserProfileTest extends BaseTest {

    @Test
    public void checkUserCanChangeProfileNameByValidName() {
        var requestBody = ChangeUserProfileRequestBody.builder()
                .name(RandomData.getValidName())
                .build();
        var responseBody =
                new ChangeUserProfileRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.ok())
                        .put(requestBody)
                        .extract()
                        .as(ChangeUserProfileResponseBody.class);

        softly.assertThat(responseBody.getCustomer().getName()).isEqualTo(requestBody.getName());
        var customerAfterRequest = this.userSteps.getCustomer(userAuthToken);
        softly.assertThat(customerAfterRequest.getName()).withFailMessage("name wasn't changed").isEqualTo(requestBody.getName());
    }


    @ParameterizedTest
    @MethodSource("invalidNames")
    public void checkUserCantChangeProfileNameByInvalidName(String name) {
        var customerBeforeRequest = this.userSteps.getCustomer(userAuthToken);

        var requestBody = ChangeUserProfileRequestBody.builder()
                .name(name)
                .build();
        new ChangeUserProfileRequest(RequestSpecs.userSpec(userAuthToken), ResponseSpecs.badRequest())
                .put(requestBody);

        var customerAfterRequest = this.userSteps.getCustomer(userAuthToken);
        assertEquals(customerBeforeRequest.getName(), customerAfterRequest.getName(), "name was changed");
    }

    private static Stream<String> invalidNames() {
        return Stream.of(
                "",
                " ",
                "   ",
                RandomData.getStringAndNumericString(10),
                String.format("%s", RandomData.getAlphabeticString(5)),
                String.format("%s ", RandomData.getAlphabeticString(5)),
                String.format("%s%s", RandomData.getValidName(), RandomData.getNumericString(1)),
                String.format("%s %s", RandomData.getNumericString(2), RandomData.getNumericString(2))
        );
    }
}
