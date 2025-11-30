package api;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.kduskov.api.steps.AccountSteps;
import ru.kduskov.api.steps.AdminSteps;
import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.common.extensions.DeleteUserTestDataExtension;
import ru.kduskov.common.extensions.TimerExtension;
import ru.kduskov.common.extensions.UserSessionExtension;
import ru.kduskov.common.storage.SessionStorage;

@ExtendWith({UserSessionExtension.class, TimerExtension.class, DeleteUserTestDataExtension.class})
public abstract class BaseTest {
    protected SoftAssertions softly;

//    @AfterEach
//    public void deleteTestData() {
//        for (var user : SessionStorage.getAllUsers()) {
//            for (var account : SessionStorage.getUserSteps(user.getUsername()).getUserAccounts())
//                AccountSteps.deleteAccount(user.getToken(), account.getId());
//        }
//        for (var user : SessionStorage.getAllUsers()) {
//            var id = new UserSteps(user.getToken()).getCustomer().getId();
//            AdminSteps.deleteUser(id);
//        }
//        SessionStorage.clear();
//    }

    @BeforeEach
    public void setUpTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void assertSoftAssertions() {
        this.softly.assertAll();
    }

}
