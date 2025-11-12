package ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.kduskov.generators.common.RandomData;
import ru.kduskov.generators.common.RequestDataGenerator;
import ru.kduskov.models.body.request.ChangeUserProfileRequestBody;
import ru.kduskov.steps.assertions.UserProfileAssertionSteps;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeUserProfileTest extends BaseTest {
    private UserProfileAssertionSteps userProfileAssertionSteps;

    @BeforeEach
    public void initAssertionClasses() {
        this.userProfileAssertionSteps = new UserProfileAssertionSteps(softly);
    }

    @Test
    public void changeNameSuccessfullyWithValidName() {
        loginWithUserCredentials();
        $(Selectors.byClassName("user-info")).click();
        $(Selectors.byText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);
        var requestBody = RequestDataGenerator.generateFilledObject(ChangeUserProfileRequestBody.class);
        var newName = requestBody.getName();

        $(Selectors.byAttribute("placeholder", "Enter new name"))
                .shouldBe(Condition.enabled)
                .pressEnter()
                .setValue(newName);
        $(Selectors.byAttribute("placeholder", "Enter new name")).shouldHave(value(newName));
        $(Selectors.byXpath("//button[contains(text(),'Save Changes')]")).click();
        var alertText = Selenide.switchTo().alert().getText();
        assertThat(alertText).isEqualTo("✅ Name updated successfully!");
        Selenide.refresh();

        $(Selectors.byXpath("//div[@class='user-info']//span[@class='user-name']")).shouldHave(text(requestBody.getName()));
        var customerAfterRequest = userSteps.getCustomer(userToken);
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(requestBody, customerAfterRequest);
    }

    @Test
    public void changeNameErrorWithInvalidName() {
        loginWithUserCredentials();
        $(Selectors.byClassName("user-info")).click();
        var oldName = $(Selectors.byXpath("//div[@class='user-info']//span[@class='user-name']")).getText();

        $(Selectors.byText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);
        var newName = RandomData.getStringAndNumericString(10);

        var nameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));

        nameInput.shouldBe(Condition.enabled)
                .click();
        nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));

        nameInput.sendKeys(Keys.DELETE);
        nameInput.setValue(newName);

        nameInput.shouldHave(value(newName));

        $(Selectors.byXpath("//button[contains(text(),'Save Changes')]")).click();
        var alertText = Selenide.switchTo().alert().getText();
        assertThat(alertText).isEqualTo("Name must contain two words with letters only");
        Selenide.refresh();

        $(Selectors.byXpath("//div[@class='user-info']//span[@class='user-name']")).shouldHave(text(oldName));
        var customerAfterRequest = userSteps.getCustomer(userToken);
        this.userProfileAssertionSteps.assertCustomerNameMatchesRequest(new ChangeUserProfileRequestBody(oldName), customerAfterRequest);
    }
}
