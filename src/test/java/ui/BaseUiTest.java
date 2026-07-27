package ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import common.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.aspectj.lang.annotation.AfterThrowing;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.MutableCapabilities;
import ru.kduskov.common.confs.Config;
import ru.kduskov.common.enums.ConfigParams;
import ru.kduskov.common.extensions.BrowserMatchExtension;

import java.util.Map;
import java.util.Objects;

@ExtendWith(BrowserMatchExtension.class)
public abstract class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.timeout = Config.getLongProperty(ConfigParams.UI_TIMEOUT);
        Configuration.remote = Config.getProperty(ConfigParams.UI_REMOTE);
        Configuration.baseUrl = Config.getProperty(ConfigParams.UI_BASE_URL);
        Configuration.browser = Config.getProperty(ConfigParams.UI_BROWSER);
        Configuration.browserSize = Config.getProperty(ConfigParams.UI_BROWSER_SIZE);
        var caps = new MutableCapabilities();
        caps.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
        Configuration.browserCapabilities = caps;
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterThrowing
    public static void makeScreenshotForFailedTest() {
        Allure.addAttachment("Screenshot", Objects.requireNonNull(Selenide.screenshot(String.valueOf(System.currentTimeMillis()))));
    }
}
