package tests;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.kduskov.models.body.response.general.AccountResponseBody;
import ru.kduskov.steps.AccountSteps;
import ru.kduskov.steps.UserSteps;

public class BaseTest {
    protected static UserSteps userSteps;
    protected static String firstUserAuthToken;
    protected static String secondUserAuthToken;
    protected SoftAssertions softly;

    @BeforeAll
    public static void setUpTestUser() {
        firstUserAuthToken = UserSteps.createRandomUser();
        secondUserAuthToken = UserSteps.createRandomUser();
        userSteps = new UserSteps();
    }

    @AfterAll
    public static void deleteUsers() {
        var users = UserSteps.getAllUsers();
        for(var user : users) {
            UserSteps.deleteUser(user.getId());
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
