package api.auth;

import common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.constants.ErrorMessages;
import ru.kduskov.api.constants.Headers;
import ru.kduskov.api.generators.LoginRequestGenerator;
import ru.kduskov.common.generators.RandomData;
import ru.kduskov.api.models.body.response.BaseResponse;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.api.steps.LoginSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.steps.StringAssertionsSteps;
import ru.kduskov.common.storage.SessionStorage;

import java.util.regex.Pattern;

import static common.Constans.FIRST_USER_ID;

public class UserApiTest extends BaseTest {

    private StringAssertionsSteps stringAssertionsSteps;

    @BeforeEach
    public void initAssertionClasses() {
        this.stringAssertionsSteps = new StringAssertionsSteps(softly);
    }

    @Test
    @UserSession
    @DisplayName("Authorization token was received when valid credentials was sent")
    public void shouldReturnAuthorizationTokenWhenValidCredentials() {
        var user = SessionStorage.getUser(FIRST_USER_ID);
        var authHeader = LoginSteps.login(
                LoginRequestGenerator.generate(user.getUsername(), user.getPassword()),
                ResponseSpecs.ok()
        ).header(Headers.AUTHORIZATION);

        this.stringAssertionsSteps.assertTextMatches(authHeader, Pattern.compile("^Basic (.+)"));
    }

    @Test
    @UserSession
    @DisplayName("Authorization header is empty when invalid credentials was sent")
    public void shouldNotReturnAuthorizationHeaderWhenInvalidCredentials() {
        var user = SessionStorage.getUser(FIRST_USER_ID);
        var loginResponse = LoginSteps.login(
                LoginRequestGenerator.generate(user.getUsername(), RandomData.getAlphabetAndNumericString(10)),
                ResponseSpecs.unauthorized()
        );
        var authHeader = loginResponse.header(Headers.AUTHORIZATION);
        var errorResponse = loginResponse.as(BaseResponse.class);
        this.stringAssertionsSteps.assertTextEqualsTo(null, authHeader);
        this.stringAssertionsSteps.assertTextEqualsTo(
                ErrorMessages.User.INVALID_USERNAME_OR_PASSWORD,
                errorResponse.getMessage()
        );
    }
    @Test
    @UserSession
    @DisplayName("Authorization header is empty when request is empty")
    public void shouldNotReturnAuthorizationHeaderWhenRequestIsEmpty() {
        var loginResponse = LoginSteps.login(null, ResponseSpecs.unauthorized());
        var authHeader = loginResponse.header(Headers.AUTHORIZATION);
        var errorResponse = loginResponse.asString();
        this.stringAssertionsSteps.assertTextEqualsTo(null, authHeader);
        this.stringAssertionsSteps.assertTextIsEmpty(errorResponse);
    }
}
