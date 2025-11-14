package api;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.kduskov.api.steps.UserSteps;

public abstract class BaseTest {
    protected static final UserSteps userSteps = new UserSteps();
    protected static String firstUserAuthToken;
    protected static String secondUserAuthToken;
    protected SoftAssertions softly;

    @BeforeAll
    public static void setUpTestUser() {
        firstUserAuthToken = userSteps.createRandomUser();
        secondUserAuthToken = userSteps.createRandomUser();
    }

    @AfterAll
    public static void deleteUsers() {
        var users = userSteps.getAllUsers();
        for(var user : users) {
            userSteps.deleteUser(user.getId());
        }
        firstUserAuthToken = null;
        secondUserAuthToken = null;
    }

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void assertSoftAssertions() {
        this.softly.assertAll();
    }

}
