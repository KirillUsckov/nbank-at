package common;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.kduskov.common.extensions.ClearTestDataExtension;
import ru.kduskov.common.extensions.TimerExtension;
import ru.kduskov.common.extensions.UserSessionExtension;
import ru.kduskov.common.steps.StringAssertionsSteps;

@ExtendWith({UserSessionExtension.class, TimerExtension.class, ClearTestDataExtension.class})
public class BaseTest {
    protected SoftAssertions softly;
    protected StringAssertionsSteps stringAssertionsSteps;

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
        this.stringAssertionsSteps = new StringAssertionsSteps(this.softly);
    }
}
