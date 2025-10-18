package steps.assertions;

import lombok.AllArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.assertions.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseAssertionsSteps {
    protected final Assertions assertions;

    public BaseAssertionsSteps(SoftAssertions softly) {
        this.assertions = new Assertions(softly);
    }

    public void assertMessage(String expectedMessage, String actualMessage) {
        assertEquals(expectedMessage, actualMessage);
    }
}
