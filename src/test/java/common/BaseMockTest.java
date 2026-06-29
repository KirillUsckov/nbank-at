package common;

import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.kduskov.mock.extensions.FraudMockExtension;
import ru.kduskov.mock.MockRunner;

@ExtendWith(FraudMockExtension.class)
@Getter
public class BaseMockTest extends BaseTest {
    @BeforeAll
    public static void setUpWireMock() {
        MockRunner.setUpWireMock();
    }

    @AfterAll
    public static void tearDownWireMock() {
        MockRunner.closeWireMock();
    }
}
