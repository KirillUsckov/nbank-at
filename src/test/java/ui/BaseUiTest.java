package ui;

import com.codeborne.selenide.Configuration;
import common.BaseTest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.MutableCapabilities;
import ru.kduskov.common.confs.Config;
import ru.kduskov.common.enums.ConfigParams;
import ru.kduskov.common.extensions.*;

import java.util.Map;


@ExtendWith(BrowserMatchExtension.class)
public abstract class BaseUiTest extends BaseTest {
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
}
