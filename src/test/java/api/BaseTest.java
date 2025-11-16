package api;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.extensions.UserSessionExtension;

@ExtendWith(UserSessionExtension.class)
public abstract class BaseTest {
    protected SoftAssertions softly;

    @AfterAll
    public static void deleteUsers() {
        var users = AdminSteps.getAllUsers();
        for(var user : users) {
            AdminSteps.deleteUser(user.getId());
        }
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
