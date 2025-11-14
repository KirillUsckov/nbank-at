package ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.MutableCapabilities;
import ru.kduskov.api.enums.Endpoint;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.models.body.request.CreateUserRequestBody;
import ru.kduskov.api.requests.skelethon.requesters.CrudRequester;
import ru.kduskov.api.specs.RequestSpecs;
import ru.kduskov.api.specs.ResponseSpecs;
import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.common.confs.Config;
import ru.kduskov.ui.pages.DashboardPage;
import ru.kduskov.ui.steps.BrowserSteps;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static ru.kduskov.common.enums.ConfigParams.*;

public abstract class BaseUiTest {

    protected static CreateUserRequestBody user;
    protected static String userToken;
    protected static final UserSteps userSteps = new UserSteps();
    protected SoftAssertions softly;
    protected final BrowserSteps browserSteps = new BrowserSteps();

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty(UI_REMOTE.getValue());
        Configuration.baseUrl = Config.getProperty(UI_BASE_URL.getValue());
        Configuration.browser = Config.getProperty(UI_BROWSER.getValue());
        Configuration.browserSize = Config.getProperty(UI_BROWSER_SIZE.getValue());
        var caps = new MutableCapabilities();
        caps.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
        Configuration.browserCapabilities = caps;
    }

    @BeforeAll
    public static void createTestUser() {
        if(user == null)
            user = RequestDataGenerator.generateFilledObject(CreateUserRequestBody.class);

        if(userToken == null) {
            userToken = userSteps.createUser(user);
            new CrudRequester(
                    RequestSpecs.userSpec(userToken),
                    ResponseSpecs.ok(),
                    Endpoint.CHANGE_USER_PROFILE
            )
                    .put(RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class));
        }
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
        new DashboardPage()
                .open()
                .waitPageOpened();
    }
}
