package tests;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.kduskov.steps.UserSteps;

public class BaseTest {
    protected static UserSteps userSteps;
    protected static String userAuthToken;
    protected SoftAssertions softly;

    @BeforeAll
    public static void setUpTestUser() {
        userAuthToken = UserSteps.createRandomUser();
        userSteps = new UserSteps();
    }

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void assertSoftAssertions() {
        this.softly.assertAll();
    }

    @AfterAll
    public static void clearTestData(){

    }
}
