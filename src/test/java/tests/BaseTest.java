package tests;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.kduskov.enums.Role;
import ru.kduskov.generators.RandomData;
import ru.kduskov.models.body.request.CreateUserRequestBody;
import ru.kduskov.requests.CreateUserRequest;
import ru.kduskov.specs.RequestSpecs;
import ru.kduskov.specs.ResponseSpecs;
import steps.UserSteps;

public class BaseTest {

    protected UserSteps userSteps;
    protected static String userAuthToken;
    protected SoftAssertions softly;

    @BeforeAll
    public static void setUpTestUser() {
        userAuthToken = UserSteps.createRandomUser();
    }

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
        this.userSteps = new UserSteps(softly);
    }

    @AfterEach
    public void afterTest() {
        this.softly.assertAll();
    }
}
