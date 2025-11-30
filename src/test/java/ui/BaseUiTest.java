package ui;

import com.codeborne.selenide.Configuration;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.MutableCapabilities;
import ru.kduskov.api.steps.AccountSteps;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.common.confs.Config;
import ru.kduskov.common.enums.ConfigParams;
import ru.kduskov.common.extensions.BrowserMatchExtension;
import ru.kduskov.common.extensions.DeleteUserTestDataExtension;
import ru.kduskov.common.extensions.TimerExtension;
import ru.kduskov.common.extensions.UserSessionExtension;
import ru.kduskov.common.storage.SessionStorage;

import java.util.Map;


@ExtendWith({UserSessionExtension.class, BrowserMatchExtension.class, TimerExtension.class, DeleteUserTestDataExtension.class})
public abstract class BaseUiTest {
    protected SoftAssertions softly;

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty(ConfigParams.UI_REMOTE);
        Configuration.baseUrl = Config.getProperty(ConfigParams.UI_BASE_URL);
        Configuration.browser = Config.getProperty(ConfigParams.UI_BROWSER);
        Configuration.browserSize = Config.getProperty(ConfigParams.UI_BROWSER_SIZE);
        var caps = new MutableCapabilities();
        caps.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
        Configuration.browserCapabilities = caps;
    }

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void assertSoftAssertions() {
        this.softly.assertAll();
    }

//    @AfterEach
//    public void deleteTestData() {
//        for (var user : SessionStorage.getAllUsers()) {
//            for (var account : SessionStorage.getUserSteps(user.getUsername()).getUserAccounts())
//                AccountSteps.deleteAccount(user.getToken(), account.getId());
//        }
//        for (var user : SessionStorage.getAllUsers()) {
//            var id = new UserSteps(user.getToken()).getCustomer().getId();
//            AdminSteps.deleteUser(id);
//        }
//        SessionStorage.clear();
//    }
}
