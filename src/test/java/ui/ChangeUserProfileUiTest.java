package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kduskov.api.generators.common.RandomData;
import ru.kduskov.api.generators.common.RequestDataGenerator;
import ru.kduskov.api.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.api.steps.assertions.UserProfileAssertionSteps;
import ru.kduskov.ui.pages.ChangeNamePage;
import ru.kduskov.ui.panels.HeaderPanel;

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
    public void changeNameSuccessfullyWithValidName() {
        loginWithUserCredentials();
        headerPanel.clickUserInfo();
        changeNamePage.waitPageOpened();

        var requestBody = RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class);
        var newName = requestBody.getName();
        changeNamePage.setNewUsername(newName).clickSaveChangesButton();

        var alertText = browserSteps.getAlertText();
        softly.assertThat(alertText).isEqualTo(NAME_UPDATED_SUCCESSFULLY.getMessage());
        browserSteps.refresh();

        var headerUsername = headerPanel.getUserNameFromUserInfo();
        softly.assertThat(headerUsername).withFailMessage("New username is not equal expected").isEqualTo(newName);
        var customerAfterRequest = userSteps.getCustomer(userToken);
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(requestBody, customerAfterRequest);
    }

    @Test
    public void changeNameErrorWithInvalidName() {
        loginWithUserCredentials();
        var oldName = headerPanel.getUserNameFromUserInfo();
        headerPanel.clickUserInfo();
        changeNamePage.waitPageOpened();

        var newName = RandomData.getStringAndNumericString(10);

        changeNamePage.setNewUsername(newName).clickSaveChangesButton();

        var alertText = browserSteps.getAlertText();
        softly.assertThat(alertText).isEqualTo(NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY.getMessage());
        browserSteps.refresh();

        var headerUsername = headerPanel.getUserNameFromUserInfo();
        softly.assertThat(headerUsername).withFailMessage("New username is not equal old name").isEqualTo(oldName);
        var customerAfterRequest = userSteps.getCustomer(userToken);
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(new ChangeUserProfileRequestBody(oldName), customerAfterRequest);
    }
}
