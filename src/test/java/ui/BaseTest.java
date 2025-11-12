package ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.MutableCapabilities;
import ru.kduskov.enums.Endpoint;
import ru.kduskov.enums.Role;
import ru.kduskov.generators.common.RequestDataGenerator;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.models.body.request.CreateUserRequestBody;
import ru.kduskov.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import ru.kduskov.steps.UserSteps;
import ru.kduskov.steps.assertions.UserProfileAssertionSteps;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;

public abstract class BaseTest {

    protected static CreateUserRequestBody user;
    protected static String userToken;
    protected static final UserSteps userSteps = new UserSteps();
    protected SoftAssertions softly;

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.56.1:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        var caps = new MutableCapabilities();
        caps.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
        Configuration.browserCapabilities = caps;
    }

    @BeforeAll
    public static void createTestUser() {
        user = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);
        user.setRole(Role.USER);
        userToken = userSteps.createUser(user);
        new CrudRequester(
                RequestSpecs.userSpec(userToken),
                ResponseSpecs.ok(),
                Endpoint.CHANGE_USER_PROFILE
        )
                .put(RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class));
    }

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void assertSoftAssertions() {
        this.softly.assertAll();
    }

    public void loginWithUserCredentials() {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", userToken);
        Selenide.open("/dashboard");
        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);
    }
}
