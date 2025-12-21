package common;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.kduskov.common.extensions.ClearSessionStorageExtension;
import ru.kduskov.common.extensions.TimerExtension;
import ru.kduskov.common.extensions.UserSessionExtension;

@ExtendWith({UserSessionExtension.class, TimerExtension.class, ClearSessionStorageExtension.class})
public class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void assertSoftAssertions() {
        this.softly.assertAll();
    }
}
