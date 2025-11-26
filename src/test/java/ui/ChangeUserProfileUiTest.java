package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.generators.common.RandomData;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.steps.UserSteps;
import ru.kduskov.api.steps.assertions.UserProfileAssertionSteps;
import ru.kduskov.common.annotations.Browser;
import ru.kduskov.common.annotations.UserSession;
import ru.kduskov.common.enums.Browsers;
import ru.kduskov.common.storage.SessionStorage;
import ru.kduskov.ui.pages.ChangeNamePage;
import ru.kduskov.ui.panels.HeaderPanel;
import ru.kduskov.ui.steps.BrowserSteps;

import static common.Constans.FIRST_USER_ID;
import static ru.kduskov.api.enums.BankAlerts.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY;
import static ru.kduskov.api.enums.BankAlerts.NAME_UPDATED_SUCCESSFULLY;

public class ChangeUserProfileUiTest extends BaseUiTest {
    private UserProfileAssertionSteps userProfileAssertionSteps;
    private final ChangeNamePage changeNamePage = new ChangeNamePage();
    private final HeaderPanel headerPanel = new HeaderPanel();

    @BeforeEach
    public void initAssertionClasses() {
        this.userProfileAssertionSteps = new UserProfileAssertionSteps(softly);
    }

    @Test
    @Browser(value = {Browsers.CHROME, Browsers.FIREFOX})
    @UserSession(isUi = true)
    public void changeNameSuccessfullyWithValidName() {
        headerPanel.clickUserInfo();
        changeNamePage.waitPageOpened();

        var requestBody = RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class);
        var newName = requestBody.getName();
        changeNamePage.setNewUsername(newName).clickSaveChangesButton();

        var alertText = BrowserSteps.getAlertText();
        softly.assertThat(alertText).isEqualTo(NAME_UPDATED_SUCCESSFULLY.getMessage());
        BrowserSteps.refresh();

        var headerUsername = headerPanel.getUserNameFromUserInfo();
        softly.assertThat(headerUsername).withFailMessage("New username is not equal expected").isEqualTo(newName);
        var customerAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getCustomer();
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(requestBody, customerAfterRequest);
    }

    @Test
    @Browser(value = {Browsers.CHROME, Browsers.FIREFOX})
    @UserSession(isUi = true)
    public void changeNameErrorWithInvalidName() {
        var oldName = headerPanel.getUserNameFromUserInfo();
        headerPanel.clickUserInfo();
        changeNamePage.waitPageOpened();

        var newName = RandomData.getStringAndNumericString(10);

        changeNamePage.setNewUsername(newName).clickSaveChangesButton();

        var alertText = BrowserSteps.getAlertText();
        softly.assertThat(alertText).isEqualTo(NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY.getMessage());
        BrowserSteps.refresh();

        var headerUsername = headerPanel.getUserNameFromUserInfo();
        softly.assertThat(headerUsername).withFailMessage("New username is not equal old name").isEqualTo(oldName);
        var customerAfterRequest = SessionStorage.getUserSteps(FIRST_USER_ID).getCustomer();
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(new ChangeUserProfileRequestBody(oldName), customerAfterRequest);
    }
}
